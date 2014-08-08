# reagent-search

A Clojure project demonstrating building interactive web applications using React and Reagent

## Summary
Both AngularJS and React provide frameworks to build modern web applications. The React approach of building web applications as components with immutable properties and mutable states fits well with Clojurescript functional programming using immutable data structures.

This project illustrates building a wikipedia search page from the scratch using Reagent. The search page is built from the following components. Each component is reusable in other web applications

* Menu - A menu component with menu and menu drop down items
* Logger - A log panel that logs messages with different log levels - debug, info, error, ...
* Search - A search autocomplete component that intelligently suggests relevant search terms by making AJAX calls to wikipedia

**TLDR:** See the application in action at [demo](http://esumitra.github.io/reagent-search)

### Frameworks and Libraries
* React
* Reagent
* Bootstrap
* core.async

## Demo
To work around browser cross origin security policies use the following configuration:
> Chrome: <i>chrome.exe --user-data-dir="C:/tmp" --disable-web-security</i>

The demo is available at [Reagent Demo](http://esumitra.github.io/reagent-search)
## License

Copyright © 2014 Edward Sumitra

Distributed under the Eclipse Public License version 1.0.
