import React from "react";
import {Button, DateFormatter, Form, Input, Message as alert, MessageBox as dialog, Pagination, Table} from "element-react";
import {Link} from "react-router-dom";

const i18n = window.i18n;
export default class PatientList extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            query: {
                query: "",
                page: 1,
                limit: 20,
                sortingField: "updatedTime",
                desc: true
            },
            data: {
                total: 0,
                page: 1,
                limit: 0,
                items: []
            },
            limitOptions: [20, 50, 100],
            columns: [
                {type: "selection"},
                                {
                    label: i18n.t("patient.doctorId"),
                    prop: "doctorId",
                    sortable: "custom"
                },
                                {
                    label: i18n.t("patient.firstName"),
                    prop: "firstName",
                    sortable: "custom"
                },
                                {
                    label: i18n.t("patient.lastName"),
                    prop: "lastName",
                    sortable: "custom"
                },
                                {
                    label: i18n.t("patient.dateOfBirth"),
                    prop: "dateOfBirth",
                    sortable: "custom"
                },
                                {
                    label: i18n.t("patient.description"),
                    prop: "description",
                    sortable: "custom"
                },
                                {
                    label: i18n.t("patient.address"),
                    prop: "address",
                    sortable: "custom"
                },
                                {
                    label: i18n.t("patient.phoneNumber"),
                    prop: "phoneNumber",
                    sortable: "custom"
                },
                                {
                    label: i18n.t("patient.title"),
                    prop: "title",
                    sortable: "custom"
                },
                                {
                    label: i18n.t("patient.gender"),
                    prop: "gender",
                    sortable: "custom"
                },
                                {
                    label: i18n.t("patient.createdTime"),
                    prop: "createdTime",
                    sortable: "custom"
                },
                                {
                    label: i18n.t("patient.createdBy"),
                    prop: "createdBy",
                    sortable: "custom"
                },
                                {
                    label: i18n.t("patient.updatedTime"),
                    prop: "updatedTime",
                    sortable: "custom"
                },
                                {
                    label: i18n.t("patient.updatedBy"),
                    prop: "updatedBy",
                    sortable: "custom"
                },
                                {
                    label: i18n.t("patient.action"),
                    width: 200,
                    fixed: "right",
                    render: function(data) {
                        return (
                            <span className="el-table__actions">
                                <Link to={{pathname: "/admin/patient/" + data.id + "/update"}}> {i18n.t("patient.update")} </Link>
                                <Button onClick={e => this.delete(data, e)} type="text">{i18n.t("patient.delete")}</Button>
                            </span>
                        );
                    }.bind(this)
                }
            ],
            selected: []
        };
    }

    componentDidMount() {
        this.find();
    }

    find() {
        window.ajax("/admin/api/patient/patient/find", {
            method: "PUT",
            body: JSON.stringify(this.state.query)
        }).then((response) => {
            this.setState({data: response});
        });
    }

    queryChange(key, value) {
        this.setState(
            {query: Object.assign(this.state.query, {[key]: value})}
        );
    }

    sortChange(data) {
        var sort = {
            sortingField: "updatedTime",
            desc: true
        };
        if (data.prop && data.order) {
            sort.sortingField = data.prop;
            sort.desc = data.order === "descending";
        }
        this.setState({query: Object.assign(this.state.query, sort)}, ()=>{
            this.find();
        });
    }

    select(selected) {
        this.setState({selected: selected});
    }

    delete(data) {
        dialog.confirm(i18n.t("patient.deleteTip"), i18n.t("patient.deleteHint"), {type: 'warning', showInput: false}).then(() => {
            window.ajax("/admin/api/patient/patient/delete", {
                method: "POST",
                body: JSON.stringify({ids: [data.id]})
            }).then(() => {
                alert({
                    type: "success",
                    message: i18n.t("patient.deleteSuccess")
                });
                this.find();
            });
        });
    }

    batchDelete() {
        dialog.confirm(i18n.t("patient.deleteTip"), i18n.t("patient.deleteHint"), {type: 'warning', showInput: false}).then(() => {
            const list = this.state.selected, ids = [];
            if (list.length === 0) {
                return;
            }
            for (let i = 0; i < list.length; i += 1) {
                ids.push(list[i].id);
            }
            window.ajax("/admin/api/patient/patient/delete", {
                method: "POST",
                body: JSON.stringify({ids: ids})
            }).then(() => {
                alert({
                    type: "success",
                    message: i18n.t("patient.deleteSuccess")
                });
                this.find();
            });
        });
    }

    render() {
        return (
            <div className="page">
                <div className="toolbar">
                    <div className="toolbar-form">
                        <Form inline={true} model={this.state.query}>
                            <Form.Item>
                                <Input value={this.state.query.name} onChange={value => this.queryChange("name", value)} icon="fa fa-search"/>
                            </Form.Item>
                            <Form.Item>
                                <Button onClick={() => this.find()}>{i18n.t("patient.search")}</Button>
                            </Form.Item>
                        </Form>
                    </div>
                    <div className="toolbar-buttons">
                        <Button type="danger" style={this.state.selected.length > 0 ? {} : {"display": "none"}} onClick={() => this.batchDelete()}>{i18n.t("patient.delete")}</Button>
                        <Link to={{pathname: "/admin/patient/create"}}><i className="fa fa-plus"></i>{i18n.t("patient.create")}</Link>
                    </div>
                </div>
                <div className="body body--full">
                    <Table style={{width: "100%"}}
                        stripe={true}
                        highlightCurrentRow={true}
                        columns={this.state.columns}
                        data={this.state.data.items}
                        onSelectChange={selected => this.select(selected)}
                        onSortChange={data => this.sortChange(data)}/>
                </div>
                <div className="footer">
                    <Pagination layout="total,sizes,prev,pager,next,jumper" total={this.state.data.total} pageSizes={this.state.limitOptions} pageSize={this.state.query.limit}
                        currentPage={this.state.query.page}
                        onSizeChange={(limit) => {
                            this.queryChange("page", 1);
                            this.queryChange("limit", limit);
                            this.find();
                        }}
                        onCurrentChange={(page) => {
                            this.queryChange("page", page);
                            this.find();
                        }}/>
                </div>
            </div>
        );
    }
}

PatientList.propTypes = {
    match: PropTypes.object.match,
    history: PropTypes.object.history
};