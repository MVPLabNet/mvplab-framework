import React from "react";
import {Breadcrumb, Button, Card, DatePicker, Form, Input} from "element-react";
import PropTypes from "prop-types";
import {Link} from "react-router-dom";

const i18n = window.i18n;
export default class PatientUpdate extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            id: this.props.match.params.id,
            form: {
                doctorId: null,
                firstName: null,
                lastName: null,
                dateOfBirth: null,
                description: null,
                address: null,
                phoneNumber: null,
                title: null,
                gender: null
            },
            titleOptions: [
                {
                    label: "MR",
                    value: "MR"
                },
                {
                    label: "MRS",
                    value: "MRS"
                },
                {
                    label: "MS",
                    value: "MS"
                },
                {
                    label: "DR",
                    value: "DR"
                }
            ],
            genderOptions: [
                {
                    label: "MALE",
                    value: "MALE"
                },
                {
                    label: "FEMALE",
                    value: "FEMALE"
                }
            ],
            rules: {
                doctorId: [{
                    required: true,
                    message: i18n.t("patient.doctorIdRule"),
                    trigger: "blur"
                }],
                firstName: [{
                    required: true,
                    message: i18n.t("patient.firstNameRule"),
                    trigger: "blur"
                }],
                lastName: [{
                    required: true,
                    message: i18n.t("patient.lastNameRule"),
                    trigger: "blur"
                }],
                dateOfBirth: [{
                    required: true,
                    type: 'date',
                    message: i18n.t("patient.dateOfBirthRule"),
                    trigger: "blur"
                }]
            }
        };
    }

    componentDidMount() {
        if (typeof this.state.id !== "undefined") {
            window.ajax("/admin/api/patient/patient/" + this.state.id)
                .then((response) => {
                    this.setState({form: response});
                });
        }
    }

    formChange(key, value) {
        const form = this.state.form;
        form[key] = value;
        this.setState(form);
    }

    update() {
        this.formRef.validate((valid) => {
            if (valid) {
                if (this.state.id) {
                    window.ajax("/admin/api/patient/patient/" + this.state.id, {
                        method: "put",
                        body: JSON.stringify(this.state.form)
                    }).then(() => {
                        this.props.history.push("/admin/patient/list");
                    });
                } else {
                    window.ajax("/admin/api/patient/patient", {
                        method: "post",
                        body: JSON.stringify(this.state.form)
                    }).then(() => {
                        this.props.history.push("/admin/patient/list");
                    });
                }
            } else {
                return false;
            }
        });
    }

    render() {
        return (
            <div className="page">
                <div className="toolbar">
                    <div className="toolbar-form">
                        <Breadcrumb separator="/">
                            <Breadcrumb.Item><Link to="/admin">{i18n.t("patient.home")}</Link></Breadcrumb.Item>
                            <Breadcrumb.Item><Link to="/admin/patient/list">{i18n.t("patient.list")}</Link></Breadcrumb.Item>
                            <Breadcrumb.Item>
                                {this.state.id
                                    ? i18n.t("patient.updatePatient")
                                    : i18n.t("patient.createPatient")}</Breadcrumb.Item>
                        </Breadcrumb>
                    </div>
                    <div className="toolbar-buttons">
                        <Button type="primary" onClick={() => this.update()}>{i18n.t("patient.save")}</Button>
                        <Link to="/admin/patient/list">{i18n.t("patient.cancel")}</Link>
                    </div>
                </div>
                <div className="body">
                    <Card>
                        <Form model={this.state.form} rules={this.state.rules} ref={(c) => {
                            this.formRef = c;
                        }} labelWidth="150">
                            <Form.Item label={i18n.t("patient.doctorId")} prop="doctorId">
                                <Input value={this.state.form.doctorId}
                                    onChange={value => this.formChange("doctorId", value)}
                                    maxLength="36"
                                />
                            </Form.Item>
                            <Form.Item label={i18n.t("patient.firstName")} prop="firstName">
                                <Input value={this.state.form.firstName}
                                    onChange={value => this.formChange("firstName", value)}
                                    maxLength="64"
                                />
                            </Form.Item>
                            <Form.Item label={i18n.t("patient.lastName")} prop="lastName">
                                <Input value={this.state.form.lastName}
                                    onChange={value => this.formChange("lastName", value)}
                                    maxLength="64"
                                />
                            </Form.Item>
                            <Form.Item label={i18n.t("patient.dateOfBirth")} prop="dateOfBirth">
                                <DatePicker value={typeof this.state.form.dateOfBirth === "string" ? new Date(this.state.form.dateOfBirth) : this.state.form.dateOfBirth}
                                    placeholder="dateOfBirth"
                                    onChange={(value) => {
                                        this.formChange("dateOfBirth", value);
                                    }}
                                />
                            </Form.Item>
                            <Form.Item label={i18n.t("patient.description")} prop="description">
                                <Input value={this.state.form.description}
                                    onChange={value => this.formChange("description", value)}
                                    maxLength="512"
                                />
                            </Form.Item>
                            <Form.Item label={i18n.t("patient.address")} prop="address">
                                <Input value={this.state.form.address}
                                    onChange={value => this.formChange("address", value)}
                                    maxLength="256"
                                />
                            </Form.Item>
                            <Form.Item label={i18n.t("patient.phoneNumber")} prop="phoneNumber">
                                <Input value={this.state.form.phoneNumber}
                                    onChange={value => this.formChange("phoneNumber", value)}
                                    maxLength="32"
                                />
                            </Form.Item>
                            <Form.Item label={i18n.t("patient.title")} prop="title">
                                <Input value={this.state.form.title}
                                    onChange={value => this.formChange("title", value)}
                                    maxLength="16"
                                />
                            </Form.Item>
                            <Form.Item label={i18n.t("patient.gender")} prop="gender">
                                <Input value={this.state.form.gender}
                                    onChange={value => this.formChange("gender", value)}
                                    maxLength="16"
                                />
                            </Form.Item>
                        </Form>
                    </Card>
                </div>
            </div>
        );
    }
}
PatientUpdate.propTypes = {
    match: PropTypes.object.match,
    history: PropTypes.object.history
};