# Simple Logger for Android

[![Build Status](https://travis-ci.org/sephiroth74/SimpleLogger.svg?branch=master)](https://travis-ci.org/sephiroth74/SimpleLogger)
<br />
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/it.sephiroth.android.library.simplelogger/simple-logger/badge.svg)](https://maven-badges.herokuapp.com/maven-central/it.sephiroth.android.library.simplelogger/simple-logger)


Installation
=================

In your project's `build.gradle` file add the following line to the `dependencies` group:

	compile 'it.sephiroth.android.library.simplelogger:simple-logger:1.0.0'


Usage
=================
Usage of the BottomNavigation widget is very easy. Just place it in your layout.xml like this:

```
LoggerFactory.Logger logger = LoggerFactory.getLogger("MainActivity", LoggerFactory.LoggerType.Console);

// optional, set a minimum filter level
// logger.setLevel(Log.INFO);

logger.info("Info message: %s", "test string");
logger.verbose("Verbose message: %d", 1);
logger.debug("Debug message: %b", false);
logger.error("Error message: %g", 1.5f);
logger.warn("Warning message");
logger.log(new IOException("test io exception"));
```

You can also use a `NullLogger`, which won't do anything, for instance to be used for your production release:

```
LoggerFactory.Logger logger = LoggerFactory.getLogger("Main", BuildConfig.DEBUG ? LoggerType.Console : LoggerTye.Null);

```

There's also a simple `FileLogger` included:

```
LoggerFactory.FileLogger fileLogger = LoggerFactory.getFileLogger("test.log");

// truncate the file
fileLogger.clear();

// level filter
fileLogger.setLevel(Log.INFO);
fileLogger.info("Info message: %s", "test string");
fileLogger.verbose("Verbose message: %d", 1); // won't be logged
fileLogger.debug("Debug message: %b", false); // won't be logged
fileLogger.error("Error message: %g", 1.5f);
fileLogger.warn("Warning message");
fileLogger.log(new IOException("test io exception")); // won't be logged
```


License
=================

The MIT License (MIT)

Copyright (c) 2016 Alessandro Crugnola

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
