# reagent-search

A Clojurescript project demonstrating building interactive web applications using React and Reagent

## Summary
Both AngularJS and React provide frameworks to build modern web applications. The React approach of building web applications as components with immutable properties and mutable states fits well with Clojurescript functional programming using immutable data structures.

This project illustrates building a wikipedia search page from the scratch using Reagent. The search page is built from the following components. Each component is reusable in other web applications

* Menu - A menu component with menu and menu drop down items
* Logger - A log panel that logs messages with different log levels - debug, info, error, ...
* Search - A search autocomplete component that intelligently suggests relevant search terms by making AJAX calls to wikipedia

**TLDR:** See the application in action at [Reagent Demo](http://esumitra.github.io/reagent-search)

### Frameworks and Libraries
* React
* Reagent
* Bootstrap
* core.async

## Demo
To work around browser cross origin security policies use the following configuration:
> Chrome: <i>chrome.exe --user-data-dir="C:/tmp" --disable-web-security</i>

## Application Design Concepts
The following design concepts are used to build the demo.
### 1. Everything is a component
Any non-trivial UX item is a react/reagent component. In the demo, the logger panel, the search input box, the typeahead drop-down are all reusable components. A component has **properties** and **state**. Properties are immutable and can be used to pass parameters that do not change for the lifetime of the component like component dimensions, size, etc. Defining a component is as simple as defining a function. The logger component can be defined as shown below

    (defn logger-component
      [props]
      [:div.footer
        [:div.container
          [:div.panel.panel-default
            [:div.panel-body.logger
              log content here ... ]]]])


### 2. A Page is a collection of composable components
The page is a collection of components mounted at the DOM ids defined in the HTML. In the example below, the navbar component is mounted at DOM id *navbar* and the logger is mounted at DOM id *logger*.

    ;; pass in navbar data as properties as the data and navbar is immutable
    (utils/mount-component reagent-search.navbar/navbar-component "navbar" @navbar-props)

    ;; pass in logger data as properties since the log channel is immutable
    (utils/mount-component reagent-search.logger/logger-component @logger-props "logger")

### 3. Application state is isolated
### 4. Components communicate through asynchronous channels (or events)

The demo is available at [Reagent Demo](http://esumitra.github.io/reagent-search)

## Wait! What about MVC or my favorite MV* pattern?
True to its name, React and consequently Reagent, is better suited to the [FRP](http://en.wikipedia.org/wiki/Functional_reactive_programming) style of building applications and not the typical MVC model. Instead of global controllers managing the interactions between models and views in a typical MVC application, the application in React/Reagent is a set of global components that interact with each other through asynchronous messages to channels.

Another way to view reagent components is that model, controllers and views are all local to the component. This gives the advantages of good code modularity and composability as components can be composed of other components while also seperating the behaviors from rendering and model/state transitions (**not expressed clearly**). While one can still use the MVC pattern at a highler level of abstraction, using an MVC pattern is not strictly necessary using an FRP programming style.

## License

Copyright © 2014 Edward Sumitra

Distributed under the Eclipse Public License version 1.0.


