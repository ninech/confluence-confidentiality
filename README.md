# confluence-confidentiality

This is a Confluence plugin that provides a confidentiality setting for each page.

![A screenshot of the plugin in action](https://i.imgur.com/w0T7zKF.png)

# What's inside

Here are the SDK commands you'll use immediately:

* atlas-run   -- installs this plugin into the product and starts it on localhost
* atlas-debug -- same as atlas-run, but allows a debugger to attach at port 5005
* atlas-cli   -- after atlas-run or atlas-debug, opens a Maven command line window:
                 - 'pi' reinstalls the plugin into the running product instance
* atlas-help  -- prints description for all commands in the SDK

Full documentation is always available at:

https://developer.atlassian.com/display/DOCS/Introduction+to+the+Atlassian+Plugin+SDK

# Whishlist

- [X] Improve style of confidentiality selector
- [ ] Full i18n (currently all confidentiality strings are not translated on the client side)
- [ ] Client code independent of `possibleConfidentialities` (so that the server entirely defines the set of possible confidentiality levels entirely)
- [ ] Admin UI (add/remove/rename possible confidentiality levels and change default confidentiality level)
- [ ] Optional per-space confidentiality set / default confidentiality
- [X] Editing of the confidentiality tied to the edit preferences of the page
- [ ] Change of confidentiality only possible within the editor
- [ ] Chosen confidentiality has impact on user roles (e.g. that certain roles can't see 'Confidential' pages or can only see 'Public' pages)

# Test the plugin locally

Run Confluence locally in Fastmode™: `atlas-run --jvmargs "-Xmx4g -XX:MaxMetaspaceSize=4g"`

Compile the project: `atlas-mvn package` (the local Confluence will automatically reload the code)

Confluence locally runs on port 1990: `http://localhost:1990/confluence/`

Use `admin:admin` as login.

# Core Files

* confluence-confidentiality.js: All of the UI logic
* confluence-confidentiality.properties: Translations (they somehow don't work client side)
* confluence-confidentiality.soy: Template for everything in the confidentiality-selector
* Confidentiality.kt: Object for server response
* ConfidentialityResource.kt: Server-Side Logic
* atlassian-plugin.xml: Resource Definition, Button Definition (change button icon etc.)

# Important Documentation Resources

* The tutorial this is based on: https://developer.atlassian.com/confdev/tutorials/adding-items-to-the-info-banner
* XSRF Token: https://developer.atlassian.com/confdev/confluence-plugin-guide/writing-confluence-plugins/form-token-handling
* Content Properties Persistence: https://developer.atlassian.com/confdev/development-resources/confluence-architecture/confluence-internals/persistence-in-confluence
* Web Item Plugin Module: https://developer.atlassian.com/confdev/confluence-plugin-guide/confluence-plugin-module-types/web-ui-modules/web-item-plugin-module
* AUI Documentation: https://docs.atlassian.com/aui/latest/docs/helper.html
* jQuery Documentation: https://api.jquery.com/jQuery.ajax/
* Soy / Closure templating reference: https://developers.google.com/closure/templates/docs/commands#if
* Kotlin Reference: https://kotlinlang.org/docs/reference/
* Atlassian Plugin SDK: https://developer.atlassian.com/docs/getting-started/set-up-the-atlassian-plugin-sdk-and-build-a-project

# How to cut a release

1. Change the version in pom.xml to `X.Y.Z`
2. `atlas-mvn clean package` (or `mvn clean package` if you don't have [Atlassian's tools](https://developer.atlassian.com/docs/getting-started/set-up-the-atlassian-plugin-sdk-and-build-a-project) installed)
3. Test the package
4. Run `export VERSION=X.Y.Z; git add pom.xml && git commit -m "Version ${VERSION}" && git tag "v${VERSION}"`
5. Change the version in pom.xml to `X.Y.z-SNAPSHOT`, where `z=Z+1`
6. Run `git add pom.xml && git commit -m "Preparing for X.Y.z"`
7. Don't forget to `git push --tags origin`
