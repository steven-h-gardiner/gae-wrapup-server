var le = {};

le.rez = {};
le.rez.action = "https://gae-wrapup-server.appspot.com/admin/wrapper/add";

le.rez.params = {};
le.rez.params.token = "wrapmixerwrap";

le.wrappers = require('./exampleWrappers.json');

le.wrappers.forEach(function(wrapper) {
  if (! wrapper.wrapper) { return; }
  console.error("WRAPPER: %j", wrapper);

  process.stdout.write([
      "curl",
      ""
  ].join(" "));
  process.stdout.write(["token", "url"].map(function(key) {
    var value = wrapper[key] || le.rez.params[key];
    return ["--data", [key, JSON.stringify(value)].join('=')].join(" ");
  }).join(" "));
  process.stdout.write([
      "",
      '--data',
      ['wrapper',
       ["'",encodeURIComponent(JSON.stringify(wrapper.wrapper)).replace(/'/g,"%27"),"'"].join("")].join("="),
      "",
      le.rez.action,
      ""
  ].join(" "));
  process.stdout.write("\n");
});
