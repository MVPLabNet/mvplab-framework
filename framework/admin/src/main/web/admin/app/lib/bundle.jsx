import React, { Component } from "react";
import PropTypes from "prop-types";
import { Loading } from "element-react";

export class Bundle extends Component {
  constructor(props) {
    super(props);
    this.state = { mod: null };
  }

  componentDidMount() {
    this.load(this.props);
  }

  load(props) {
    this.setState({ mod: null });
    const path = "/" + props.bundle.scriptFile;
    if (this.exist(path)) {
      this.loadI18n(props);
    } else {
      const script = document.createElement("script");
      script.src = path;
      document.querySelector("body").appendChild(script);
      script.onload = () => {
        this.loadI18n(props);
      };
    }
  }

  loadI18n(props) {
    window.ajax("/admin/api/bundle/" + props.bundle.name + "/message/" + document.querySelector("html").getAttribute("lang"), { method: "GET" })
      .then((response) => {
        window.ElementUI.i18n.use(response);
      })
      .then(() => this.setState({ mod: window[props.bundle.name].default }));
  }

  exist(scriptSrc) {
    const scripts = document.querySelectorAll("script");
    for (let i = 0; i < scripts.length; i += 1) {
      if (scripts[i].src.indexOf(scriptSrc) >= 0) {
        return true;
      }
    }
    return false;
  }

  render() {
    return this.state.mod
      ? React.createElement(this.state.mod)
      : <Loading className="loading" fullscreen={true} />;
  }
}

Bundle.propTypes = { bundle: PropTypes.object };
