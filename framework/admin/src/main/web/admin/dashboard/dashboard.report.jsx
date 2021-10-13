import React from "react";
import { Breadcrumb, Card, Layout } from "element-react";
import "./dashboard.report.css";

const bundle = window.app.bundle("dashboardBundle");

const i18n = window.i18n;
export default class DashboardReport extends React.Component {
  constructor(props) {
    super(props);
    this.state = { dashboards: bundle.dashboards };
  }

  render() {
    return <div className="page">
      <div className="toolbar">
        <div className="toolbar-form">
          <Breadcrumb separator="/">
            <Breadcrumb.Item><h1 className="toolbar-header">Dashboard</h1></Breadcrumb.Item>
          </Breadcrumb>
        </div>
        <div className="toolbar-buttons">
        </div>
      </div>
      <div className="body">
        <Layout.Row>
          <Layout.Col span="12">
            <div className="dashboard-report-list">
              {this.state.dashboards.map(dashboard =>
                <div key={dashboard.name} className="dashboard-report-list__item">
                  <Card header={i18n.t(dashboard.messageKey) || dashboard.name}>
                    {React.createElement(dashboard.component)}
                  </Card>
                </div>
              )}
            </div>
          </Layout.Col>
        </Layout.Row>
      </div>
    </div>;
  }
}
