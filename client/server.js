var express = require("express");
var app = express();
var history = require("connect-history-api-fallback");
var enforce = require('express-sslify');

// allow to call history-mode route
app.use(history());

// Redirect http to https (with "trustProtoHeader" because the app will be behind a LB)
app.use(enforce.HTTPS({ trustProtoHeader: true }));

// Serve all the files in '/build' directory
app.use(express.static("build"));

app.listen(process.env.PORT, "0.0.0.0", function() {
  console.info(`Mocky is running on port ${process.env.PORT}`);
});


