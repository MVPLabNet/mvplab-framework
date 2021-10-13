import React from "react";
import { Route, RouteExtension } from "react-router-dom";
import {
  Area,
  AreaChart,
  Bar,
  BarChart,
  Brush,
  CartesianAxis,
  CartesianGrid,
  Cell,
  ComposedChart,
  Cross,
  Curve,
  Dot,
  ErrorBar,
  Label,
  LabelList,
  Layer,
  Legend,
  Line,
  LineChart,
  Pie,
  PieChart,
  PolarAngleAxis,
  PolarGrid,
  PolarRadiusAxis,
  Polygon,
  Radar,
  RadarChart,
  RadialBar,
  RadialBarChart,
  Rectangle,
  ReferenceAreakl,
  ReferenceDot,
  ReferenceLine,
  ResponsiveContainer,
  Sankey,
  Scatter,
  ScatterChart,
  Sector,
  Surface,
  Symbols,
  Text,
  Tooltip,
  Treemap,
  XAxis,
  YAxis,
  ZAxis
} from "recharts";
import DashboardReport from "./dashboard.report";


window.Recharts = {
  Area,
  AreaChart,
  Bar,
  BarChart,
  Brush,
  CartesianAxis,
  CartesianGrid,
  Cell,
  ComposedChart,
  Cross,
  Curve,
  Dot,
  ErrorBar,
  Label,
  LabelList,
  Layer,
  Legend,
  Line,
  LineChart,
  Pie,
  PieChart,
  PolarAngleAxis,
  PolarGrid,
  PolarRadiusAxis,
  Polygon,
  Radar,
  RadarChart,
  RadialBar,
  RadialBarChart,
  Rectangle,
  ReferenceAreakl,
  ReferenceDot,
  ReferenceLine,
  ResponsiveContainer,
  Sankey,
  Scatter,
  ScatterChart,
  Sector,
  Surface,
  Symbols,
  Text,
  Tooltip,
  Treemap,
  XAxis,
  YAxis,
  ZAxis
};

Object.assign(window.app.bundle("dashboardBundle"), {
  dashboards: [],
  addDashboard: function(dashboard) {
    this.dashboards.push(dashboard);
    return this;
  }
});

export default function Dashboard() {
  return (
    <div>
      <Route path="/admin/dashboard/report" component={DashboardReport} />
      <RouteExtension bundle="dashboardBundle" />
    </div>
  );
}

