import React, {Component} from "react";
import {Button, Form, i18n as elementI18n, Input, Layout, Loading, Select} from "element-react";
import ReactDOM from "react-dom";

import "./css/theme.css";
import "./css/main.css";
import "./fetch";

const i18n = {
    locale: {},
    use(lang) {
        Object.assign(this.locale, lang);
        elementI18n.use(this.locale);
    },
    t(path, options) {
        try {
            return elementI18n.t(path, options);
        } catch (e) {
            window.console.log("missing message, key=" + path);
            return null;
        }
    }
};

i18n.use(window.app.messages);

function queryParams(search) {
    const vars = search.split("&");
    const query = {};
    for (let i = 0; i < vars.length; i += 1) {
        const pair = vars[i].split("=");
        const key = decodeURIComponent(pair[0]);
        const value = decodeURIComponent(pair[1]);
        // If first entry with this name
        if (typeof query[key] === "undefined") {
            query[key] = decodeURIComponent(value);
            // If second entry with this name
        } else if (typeof query[key] === "string") {
            const arr = [query[key], decodeURIComponent(value)];
            query[key] = arr;
            // If third or later entry with this name
        } else {
            query[key].push(decodeURIComponent(value));
        }
    }
    return query;
}

class App extends Component {
    constructor(props) {
        super(props);

        let language = "en-US";
        if (window.location.search) {
            language = queryParams(window.location.search.substring(1)).language;
        }
        this.state = {
            rules: {
                name: [{
                    required: true,
                    trigger: "blur",
                    message: i18n.t("setup.nameMessage")
                }],
                userUsername: [{
                    required: true,
                    trigger: "blur",
                    message: i18n.t("setup.userUsernameMessage")
                }],
                userPassword: [{
                    required: true,
                    trigger: "blur",
                    message: i18n.t("setup.userPasswordMessage")
                }]
            },
            form: Object.assign({
                name: "",
                language: language,
                databaseVendor: "h2",
                databaseHost: null,
                databasePort: null,
                databaseDatabase: null,
                databaseUsername: null,
                databasePassword: null,
                userUsername: "",
                userPassword: "",
                userEmail: "",
                smtpHost: "",
                smtpPort: "",
                smtpUsername: "",
                smtpPassword: ""
            }, window.localStorage.form ? JSON.parse(window.localStorage.form) : {}, {
                userPassword: "",
                smtpPassword: ""
            }),
            isHsql: true,
            loading: false
        };
    }

    change(value, name) {
        const form = this.state.form;
        form[name] = value;
        this.setState({form});
        if (name === "databaseVendor") {
            this.toggleDatabaseCollapse(value);
        }
        if (name === "language") {
            window.localStorage.form = JSON.stringify(this.state.form);
            if (window.location.href.includes("?language=")) {
                window.location.href = window.location.href.replace(new RegExp("\\?.*"), "?language=" + value);
            } else {
                window.location.href = window.location.href + "?language=" + value;
            }
        }
    }

    toggleDatabaseCollapse(vendor) {
        this.setState({isHsql: vendor === "hsql"});
    }

    submit() {
        this.setupForm.validate((valid) => {
            const form = this.state.form;
            const data = {
                name: form.name,
                language: form.language,
                database: {
                    vendor: form.databaseVendor,
                    host: form.databaseHost,
                    port: form.databasePort,
                    database: form.databaseDatabase,
                    username: form.databaseUsername,
                    password: form.databasePassword
                },
                user: {
                    username: form.userUsername,
                    password: form.userPassword,
                    email: form.userEmail
                },
                smtp: {
                    host: form.smtpHost,
                    port: form.smtpPort,
                    username: form.smtpUsername,
                    password: form.smtpPassword
                }
            };
            if (valid) {
                this.setState({loading: true});
                fetch("/web/api/install", {
                    method: "POST",
                    body: JSON.stringify(data),
                    headers: {
                        "Content-Type": "application/json",
                        "Accept": "application/json"
                    }
                }).then(() => {
                    this.check();
                });
            } else {
                return false;
            }
        });
    }

    check() {
        fetch("/health-check", {method: "GET"}).then(() => {
            window.localStorage.form = null;
            window.location.href = "";
        }, () => {
            setTimeout(() => this.check(), 1000);
        });
    }

    render() {
        return (
            <div>
                {this.state.loading && <Loading fullscreen={true}/>}
                <Layout.Row className="setup">
                    <Layout.Col span="16" offset="4">
                        <h1 className="setup__title">{i18n.t("setup.title")}</h1>
                        <Form model={this.state.form} labelWidth="120" onSubmit={() => this.submit()} rules={this.state.rules} ref={(c) => {
                            this.setupForm = c;
                        }}>
                            <h3>{i18n.t("setup.basic")}</h3>
                            <Form.Item label={i18n.t("setup.language")}>
                                <Select value={this.state.form.language} onChange={value => this.change(value, "language")}>
                                    <Select.Option key="zh-CN" label="中文" value="zh-CN"/>
                                    <Select.Option key="en-US" label="English" value="en-US"/>
                                </Select>
                            </Form.Item>
                            <Form.Item label={i18n.t("setup.name")} prop="name">
                                <Input value={this.state.form.name} onChange={value => this.change(value, "name")}/>
                            </Form.Item>
                            <hr/>
                            <h3>{i18n.t("setup.database")}</h3>
                            <Form.Item label={i18n.t("setup.databaseVendor")}>
                                <Select value={this.state.form.databaseVendor} onChange={value => this.change(value, "databaseVendor")}>
                                    <Select.Option key="h2" label="H2" value="h2"/>
                                    <Select.Option key="mysql" label="MySQL" value="mysql"/>
                                </Select>
                            </Form.Item>
                            <div className={"setup__database-addition " + (this.state.isHsql ? "" : "active")}>
                                <Form.Item label={i18n.t("setup.databaseHost")}>
                                    <Input value={this.state.form.databaseHost} onChange={value => this.change(value, "databaseHost")}/>
                                </Form.Item>
                                <Form.Item label={i18n.t("setup.databasePort")}>
                                    <Input value={this.state.form.databasePort} onChange={value => this.change(value, "databasePort")}/>
                                </Form.Item>
                                <Form.Item label={i18n.t("setup.databaseDatabase")}>
                                    <Input value={this.state.form.databaseDatabase} onChange={value => this.change(value, "databaseDatabase")}/>
                                </Form.Item>
                                <Form.Item label={i18n.t("setup.databaseUsername")}>
                                    <Input value={this.state.form.databaseUsername} onChange={value => this.change(value, "databaseUsername")}/>
                                </Form.Item>
                                <Form.Item label={i18n.t("setup.databasePassword")}>
                                    <Input value={this.state.form.databasePassword} onChange={value => this.change(value, "databasePassword")}/>
                                </Form.Item>
                            </div>
                            <hr/>
                            <h3>{i18n.t("setup.user")}</h3>
                            <Form.Item label={i18n.t("setup.userUsername")} prop="userUsername">
                                <Input value={this.state.form.userUsername} onChange={value => this.change(value, "userUsername")}/>
                            </Form.Item>
                            <Form.Item label={i18n.t("setup.userPassword")} prop="userPassword">
                                <Input value={this.state.form.userPassword} onChange={value => this.change(value, "userPassword")} type="password"/>
                            </Form.Item>
                            <Form.Item label="">
                                <Button type="primary" onClick={() => this.submit()}>{i18n.t("setup.install")}</Button>
                            </Form.Item>
                        </Form>
                    </Layout.Col>
                </Layout.Row>
            </div>

        );
    }
}

ReactDOM.render(<App/>, document.getElementById("app"));
