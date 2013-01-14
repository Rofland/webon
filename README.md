# What is Webon #

You may have heard of some tools that monitor CPU usage, memory consumption, file descriptor counts, etc. But that is too little. Is my application working correctly? What has been done? What is being processed? What is in the queue?

Webon is a Java+JavaScript library bringing you the capability and simplicity to address the above questions and, without exaggeration, to monitor literally anything inside your application.

# How Does Webon Work #

At the core of Webon is a hierarchy of named nodes. A node is either a branch node (containing child nodes) or leaf node (containing exposed application state). Each node can be identified by its path down the root. The hierarchy is made visible to an embedded Web server (Jetty) through a servlet.

A Web page monitors application states by subscribing to the nodes via Webon's JavaScript library. The presentation of nodes is (and should be) application customizable.

# How to Use Webon #

As an application developer, you do two things.
 1. Create and maintain the hierarchy of nodes (via Java library).
 2. Write monitoring pages (via JavaScript library).

Please refer to example/webon/Example.java and web/index.html for detail. The simplest way to run the supplied example is to
 1. Import Webon as Eclipse project and Run example/webon.Example
 2. Open http://localhost:8300/ in JavaScript enabled browser.
