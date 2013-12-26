#
# The MIT License (MIT)
#
# Copyright (c) 2013 Michal Turek
# This file is part of TODOs Plugin (Jenkins CI).
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.
#


###############################################################################
#### Howto

# https://wiki.jenkins-ci.org/display/JENKINS/Plugin+tutorial
# https://wiki.jenkins-ci.org/display/~martino/2011/10/27/The+JenkinsPluginTotallySimpelGuide
# http://plugin-generator.jenkins-ci.org/
# https://wiki.jenkins-ci.org/display/JENKINS/Hosting+Plugins


###############################################################################
#### Configuration

JENKINS_PORT = 9000
DEBUGGER_PORT = 8000
TOUCH_MVN_REPOSITORIES = true
SKIP_TESTS = false

ifeq ($(TOUCH_MVN_REPOSITORIES), true)
MVN_FLAGS =
else
MVN_FLAGS = -o
endif

ifeq ($(SKIP_TESTS), true)
SKIP_TESTS_OPT = -Dmaven.test.skip=true -DskipTests=true
else
SKIP_TESTS_OPT =
endif


###############################################################################
#### Generate plugin

# http://plugin-generator.jenkins-ci.org/


###############################################################################
#### Create an Eclipse project

.PHONY: eclipse
eclipse:
	mvn -DdownloadSources=true -DdownloadJavadocs=true -DoutputDirectory=target/eclipse-classes eclipse:eclipse


###############################################################################
#### Compile

.PHONY: compile
compile:
	# mvn generate-sources
	mvn compile


###############################################################################
#### Build the plugin

.PHONY: install
install:
	mvn $(SKIP_TESTS_OPT) install


###############################################################################
#### Run, http://localhost:9000/jenkins

.PHONY: run
run:
	# java -jar jenkins.war --httpPort=$(JENKINS_PORT)
	MAVEN_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=$(DEBUGGER_PORT),suspend=n" mvn $(MVN_FLAGS) hpi:run -Djetty.port=$(JENKINS_PORT)


###############################################################################
#### Search missing translations

# https://wiki.jenkins-ci.org/display/JENKINS/Internationalization
# native2ascii

.PHONY: locale
locale:
	mvn stapler:i18n -Dlocale=cs
	mvn stapler:i18n -Dlocale=ru


###############################################################################
#### Package

.PHONY: package
package:
	# To fix failing tests during mvn package.
	# mvn -DargLine="-Djna.nosys=true" $(SKIP_TESTS_OPT) package
	mvn $(SKIP_TESTS_OPT) package


###############################################################################
#### Clean

.PHONY: clean
clean:
	rm -rf target work
