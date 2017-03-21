1. `git clone ssh://${USERNAME}@transit.apt.ri.cmu.edu/usr2/sgardine/repos/smartwrap.git`
2. cd smartwrap/plugin
3. `ant xpi`
4. This command generates dist/smartwrapClient.xpi
5. open Firefox
6. Tools->Add-Ons->Extensions
7. Drag and drop dist/smartwrapClient.xpi from the filesystem onto the Extensions pane of Firefox
8. Accept all permissions

To install from Mozilla:

1. Open Firefox
2. Tools -> Add-ons
3. Choose the Extensions pane
4. Search for "smartwrap"
5. Scroll to bottom of results, click on "See all X results" (X was 161 for me).
6. First result is "SmartWrap Client", click on "Add to Firefox" button.
