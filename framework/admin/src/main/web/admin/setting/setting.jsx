import React from "react";
import { RouteExtension } from "react-router-dom";


Object.assign(window.app.bundle("settingBundle"), {
  dashboards: [],
  addDashboard: function(dashboard) {
    this.dashboards.push(dashboard);
    return this;
  }
});

export default function Setting() {
  return (
    <div>
      <RouteExtension bundle="settingBundle" />
    </div>
  );
}
