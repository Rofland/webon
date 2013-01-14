function OneOff() {
	this.beat = function(hit, time) {
		this.obsolete = true;
	}
}

function Forever() {
	this.beat = function(hit, time) {
	}
}

function LiveTillHit(deadline) {
	this.beat = function(hit, time) {
		if (hit || (deadline && deadline <= time))
			this.obsolete = true;
	}
}

function DeadIfMiss(probation) {
	var deadline = 0;

	this.beat = function(hit, time) {
		if (hit)
			deadline = 0;
		else if (!probation)
			this.obsolete = true;
		else if (deadline == 0)
			deadline = time + probation;
		else if (deadline <= time)
			this.obsolete = true;
	}
}

function Channel(key) {
	var audiences = [];

	this.accept = function(callback, loyalty) {
		audiences.push({
			loyalty : loyalty ? loyalty : new Forever(),
			update : callback
		});
	}

	this.cleanup = function() {
		var j = 0;
		for ( var i = 0; i < audiences.length; i++) {
			var audience = audiences[i];
			if (!audience.loyalty.obsolete)
				audiences[j++] = audience;
		}
		while (j < audiences.length)
			audiences.pop();
	}

	this.isEmpty = function() {
		return audiences.length == 0;
	}

	this.update = function(value, time) {
		for ( var i = 0; i < audiences.length; i++) {
			var audience = audiences[i];
			if (!audience.loyalty.obsolete) {
				audience.loyalty.beat(value != null, time);
				audience.update(key, value);
			}
		}
	}

}

function Query(url, param) {
	this.url = url;
	this.type = "POST";
	this.dataType = "json";
	this.traditional = true;
	this.data = param ? param : {};
}

function Source(query, interval) {
	var _pool = {};

	this.relate = function(key, callback, loyalty) {
		var channel = _pool[key];
		if (!channel) {
			channel = new Channel(key);
			_pool[key] = channel;
		}
		channel.accept(callback, loyalty);
	}

	var running = false;
	function run() {
		if (running)
			return;
		var keys = [];
		for ( var key in _pool)
			keys.push(key);
		if (keys.length == 0)
			return;

		query.data.key = keys;
		running = true;
		$.ajax(query).done(function(values) {
			var time = new Date().getTime();
			for ( var i = 0; i < keys.length; i++) {
				var value = values[i];
				var key = keys[i];
				var channel = _pool[key];
				channel.update(value, time);
				channel.cleanup();
				if (channel.isEmpty())
					delete _pool[key];
			}
		}).always(function() {
			running = false;
		});
	}

	this.load = run;
	setInterval(run, interval);
}

function Literal(name, $e) {
	var index;
	var $e;
	var state;

	this.setSchema = function(type, schema) {
		index = schema.indexOf(name);
		if (index < 0)
			return;
		if (state)
			$e.empty().append(state[i]);
		return;
	}

	this.update = function(newState) {
		if (index < 0)
			return;
		$e.empty().append("" + newState[index]);
		state = newState;
	}
}

function Suite($parts, views) {
	this.setSchema = function(type, schema) {
		for ( var i = 0; i < views.length; i++)
			views[i].setSchema(type, schema);
	}

	this.update = function(state) {
		for ( var i = 0; i < views.length; i++)
			views[i].update(state);
	}

	this.dismiss = function() {
		$parts.remove();
	}

	this.hide = function() {
		$parts.hide();
	}

	this.show = function() {
		$parts.show();
	}
}

function Weblet() {
	this.obsolete = false;

	var _type = null;
	var _schema = null;
	var _state = null;
	var _suites = [];

	this.update = function(state) {
		_state = state;
		if (!_type || this.obsolete)
			return;

		var j = 0;
		for ( var i = 0; i < _suites.length; i++) {
			var suite = _suites[i];
			if (suite.obsolete)
				continue;
			suite.update(state);
			_suites[j++] = suite;
		}
		while (j < _suites.length)
			_suites.pop();
		if (j == 0)
			this.obsolete = true;
	}

	this.setSchema = function(type, schema) {
		_type = type;
		_schema = schema;
		for ( var i = 0; i < _suites.length; i++)
			_suites[i].setSchema(type, schema);
	}

	this.showOn = function(suite) {
		for ( var i = 0; i < _suites.length; i++)
			if (_suites[i] == suite)
				return;
		this.obsolete = false;
		_suites.push(suite);
		if (_type)
			suite.setSchema(_type, _schema);
		if (_state)
			suite.update(_state);
	}
}

function Webon(schemaURL, stateURL) {
	var schemaSrc = new Source(new Query(schemaURL, {
		aspect : "schema"
	}), 1000);

	var stateSrc = new Source(new Query(stateURL, {
		aspect : "state"
	}), 5000);

	var weblets = {}
	this.include = function(path) {
		var weblet = weblets[path];
		if (!weblet) {
			weblet = new Weblet();

			function updateState(path, state) {
				weblet.update(state);
			}

			schemaSrc.relate(path, function(path, schema) {
				var type = schema.pop();
				weblet.setSchema(type, schema);
				stateSrc.relate(path, updateState);
			}, new LiveTillHit(30));
			weblets[path] = weblet;
		}
		return weblet;
	}
}

function html2Suite(html, viewIndicator, $container) {
	var $parts = $(html);
	var views = [];

	var $root = $("<div>").append($parts);
	$("." + viewIndicator, $root).each(function(i, e) {
		var $e = $(e);
		var name = $e.attr("name");
		views.push(new Literal(name, $e));
	});
	$container.append($parts);
	return new Suite($parts, views);
}

function ManifestSuite($e) {
	var views = [];

	this.setSchema = function(type, schema) {
		$e.empty();
		$type = $("<div style='font-weight:bold; text-align:center'>").append(type);
		$e.append($type);
		for ( var i = 0; i < schema.length; i++) {
			var $name = $("<div style='display:inline-block; width:30%'>").append(schema[i]);
			var $value = $("<div style='display:inline-block; text-align:right; width: 70%'>?</div>");
			var $attr = $("<div>").append($name).append($value);
			$e.append($attr);
			var view = new Literal(schema[i], $value);
			view.setSchema(type, schema);
			views.push(view);
		}
	}

	this.update = function(state) {
		for ( var i = 0; i < views.length; i++)
			views[i].update(state);
	}

	this.dismiss = function() {
		$e.remove();
	}

	this.hide = function() {
		$e.hide();
	}

	this.show = function() {
		$e.show();
	}
}
