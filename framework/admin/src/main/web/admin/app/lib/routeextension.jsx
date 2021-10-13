import React, { Component } from "react";
import { Route } from "react-router-dom";
import PropTypes from "prop-types";

export default class RouteExtension extends Component {
  constructor(props) {
    super(props);
    this.state = { routes: {} };
  }

  componentDidMount() {
    const routes = window.app.bundle(this.props.bundle).routes;
    this.setState({ routes });
  }

  render() {
    return Object.keys(this.state.routes).map((path) => {
      const bundleName = this.state.routes[path];
      if (!window[bundleName]) {
        window.console.log("missing bundle, path=" + path + ", name=" + bundleName);
        return "";
      }
      const bundle = window[bundleName].default;
      return <Route key={path} path={path} component={bundle} />;
    });
  }
}

RouteExtension.propTypes = { bundle: PropTypes.string };