# jshint-for-netbeans
A plugin for NetBeans IDE.

Creates Action Items out of [JSHint](http://jshint.com/) errors.

Acquires JSHint errors by linting files that end with ".js" with an embedded version of JSHint against configuration files, named .jshintrc.

Looks up configuration files the same way [JSHint for npm](https://www.npmjs.com/package/jshint) does it, by running recursive searches from the directory of the actual JS file and up to the filesystem root.

## License
[MIT](https://raw.githubusercontent.com/luka-zitnik/jshint-for-netbeans/master/LICENSE), apart from jshint.js, distributed under [JSLint license](https://github.com/jshint/jshint/blob/master/src/jshint.js), and rhino.jar, distributed under [MPL 2.0](https://www.mozilla.org/MPL/).

## Useful links
- http://bits.netbeans.org/dev/javadoc/
- http://www-archive.mozilla.org/rhino/apidocs/
- https://developer.mozilla.org/en-US/docs/Mozilla/Projects/Rhino/Scopes_and_Contexts
- http://www.javaworld.com/article/2073352/core-java/simply-singleton.html
- https://casecurity.org/wp-content/uploads/2013/10/CASC-Code-Signing.pdf
- http://wiki.netbeans.org/DevFaqSignNbm
- https://github.com/lomatek/netbeans-jslint
- http://plugins.netbeans.org/plugin/40893/jslint
- http://plugins.netbeans.org/plugin/52226/jshint
- http://jshint.com/docs/
