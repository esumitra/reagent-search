# reagent-search

A Clojurescript project demonstrating building interactive web applications using React and Reagent

## Summary
Both AngularJS and React provide frameworks to build modern web applications. The React approach of building web applications as components with immutable properties and mutable states fits well with Clojurescript functional programming using immutable data structures.

This project illustrates building a wikipedia search page from the scratch using Reagent. The search page is built from the following components. Each component is reusable in other components, pages or web applications.

* Menu - A menu component with menu and menu drop down items
* Logger - A log panel that logs messages with different log levels - debug, info, error, ...
* Search - A search autocomplete component that intelligently suggests relevant search terms by making AJAX calls to wikipedia
* Search preview - A preview panel that summarizes the current item selected in auto complete suggestions

**TLDR:** See the application in action at [Reagent Demo](http://esumitra.github.io/reagent-search)

### Frameworks and Libraries
* React and Reagent for defining components
* Bootstrap for component CSS
* core.async for data flow across components

## Demo
To work around browser cross origin security policies (CORS) for the demo use the following configuration:
> Chrome: <i>chrome.exe --user-data-dir="C:/tmp" --disable-web-security</i>

The demo can be accessed live at [Reagent Demo](http://esumitra.github.io/reagent-search)

## Application Design Concepts
The following design concepts describe detail guidelines on building react and reagent applications.
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

### 3. Component local state is isolated
Data required to display a component is specified as local reagent atoms.  This enables the display of components to be fully independent of any external states leading to better modularity.  E.g., the auto complete suggestions list component needs to display a list of suggestions which is defined as a local atom with an initial value of an empty list.

    (defn search-component
      [props]
      (let autocomplete-items (atom []) ;; list of autocomplete items which will be populated by ajax call
      ...))

Local state is changed through a handler function as shown below:

    (defn- handle-querystream
      "gets autocomplete data from query"
      [chan-query chan-log ref-ac-items]
       (let [autocomplete-terms (<! (wiki/get-autocomplete-terms q))]
         (reset! ref-ac-items autocomplete-terms)) ;; local state changed here!
       ...))

### 4. Components communicate events and data through asynchronous channels
Since components isolate state in local state atoms, changes to local state are affected through handler functions that respond or react to actions and events in asynchronous channels that are shared across components . E.g., the handler function to update the list of suggestions reacts to changes in the input query channel and fetches a new list of suggestions through an AJAX call to the wikipedia API. The function then updates the local state of to be the new list of fetched suggestions.

    (defn- handle-querystream
      "gets autocomplete data from query"
      [chan-query chan-log ref-ac-items]
      (go-loop
       []
       (let [q (<! chan-query) ;; listen to data in query channel
             autocomplete-terms (<! (wiki/get-autocomplete-terms q))]
         (if (= ["error"] autocomplete-terms)
           (slogger/error-message chan-log "server error")
           (slogger/server-message chan-log (str autocomplete-terms)))
         (reset! ref-ac-items autocomplete-terms)) ;; update local state
       (recur)))

By isolating the display of data required to display a component into local state atoms and defining handler functions to define how the data changes, one can separate the display of a component from describing how the data changes i.e., a good separation of concerns leading to better modularity.

### 5. Minimal use of global application state
Since all the state required to display a component is isolated in component local state, the global application state is used only for channels shared across components and properties and initial state values for each individual component if needed. Minimal use of global application state leads to an application that can be debugged and maintained easily.

## Wait! What about MVC or my favorite MV* pattern?
True to its name, React and consequently Reagent, is better suited to the [FRP](http://en.wikipedia.org/wiki/Functional_reactive_programming) style of building applications and not the typical MVC model. Instead of global controllers managing the interactions between models and views in a typical MVC application, the application in React/Reagent is a set of  components that are fully isolated through mutable local state and immutable properties that interact with each other through asynchronous actions and messages to channels.

Building a web application using these design guidelines gives the advantages of good code modularity and composability.  Components behavior is separated component display enabling easy and robust composability of components. Using an MVC pattern is not strictly necessary to build complex highly interactive web application using an FRP programming style.

## License

Copyright © 2014 Edward Sumitra

Distributed under the Eclipse Public License version 1.0.
