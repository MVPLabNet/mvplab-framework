import React from "react";
import {Breadcrumb, Button, Card, Form} from "element-react";
import PropTypes from "prop-types";
import {Link} from "react-router-dom";

const i18n = window.i18n;
export default class PatientView extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            id: this.props.match.params.id,
            form: {
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

    render() {
        return (
            <div className="page">
                <div className="toolbar">
                    <div className="toolbar-form">
                        <Breadcrumb separator="/">
                            <Breadcrumb.Item><Link to="/admin">{i18n.t("patient.home")}</Link></Breadcrumb.Item>
                            <Breadcrumb.Item><Link to="/admin/patient/list">{i18n.t("patient.list")}</Link></Breadcrumb.Item>
                            <Breadcrumb.Item>{i18n.t("patient.viewPatient")}</Breadcrumb.Item>
                        </Breadcrumb>
                    </div>
                    <div className="toolbar-buttons">
                        <Link to="/admin/patient/list">{i18n.t("patient.cancel")}</Link>
                    </div>
                </div>
                <div className="body">
                    <Card>
                                                <Form.Item label={i18n.t("patient.doctorId")} prop="doctorId">
                            {this.state.form.doctorId}
                        </Form.Item>
                                                <Form.Item label={i18n.t("patient.firstName")} prop="firstName">
                            {this.state.form.firstName}
                        </Form.Item>
                                                <Form.Item label={i18n.t("patient.lastName")} prop="lastName">
                            {this.state.form.lastName}
                        </Form.Item>
                                                <Form.Item label={i18n.t("patient.dateOfBirth")} prop="dateOfBirth">
                            {this.state.form.dateOfBirth}
                        </Form.Item>
                                                <Form.Item label={i18n.t("patient.description")} prop="description">
                            {this.state.form.description}
                        </Form.Item>
                                                <Form.Item label={i18n.t("patient.address")} prop="address">
                            {this.state.form.address}
                        </Form.Item>
                                                <Form.Item label={i18n.t("patient.phoneNumber")} prop="phoneNumber">
                            {this.state.form.phoneNumber}
                        </Form.Item>
                                                <Form.Item label={i18n.t("patient.title")} prop="title">
                            {this.state.form.title}
                        </Form.Item>
                                                <Form.Item label={i18n.t("patient.gender")} prop="gender">
                            {this.state.form.gender}
                        </Form.Item>
                                                <Form.Item label={i18n.t("patient.createdTime")} prop="createdTime">
                            {this.state.form.createdTime}
                        </Form.Item>
                                                <Form.Item label={i18n.t("patient.createdBy")} prop="createdBy">
                            {this.state.form.createdBy}
                        </Form.Item>
                                                <Form.Item label={i18n.t("patient.updatedTime")} prop="updatedTime">
                            {this.state.form.updatedTime}
                        </Form.Item>
                                                <Form.Item label={i18n.t("patient.updatedBy")} prop="updatedBy">
                            {this.state.form.updatedBy}
                        </Form.Item>
                                            </Card>
                </div>
            </div>
        );
    }
}

PatientView.propTypes = {
    match: PropTypes.object.match,
    history: PropTypes.object.history
};