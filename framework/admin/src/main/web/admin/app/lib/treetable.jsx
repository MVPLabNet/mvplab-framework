import React from "react";
import PropTypes from "prop-types";
import { Table } from "element-react";
import "../css/tree-table.css";

const foldList = [];
const loadList = [];
let needInitFlag = false;
let initDepth = 2;
export default class TreeTable extends React.Component {
  constructor(props) {
    super(props);
    let data = [];
    if (props.treeData && props.treeData.length > 0) {
      data = this.initData(props.treeData);
    } else {
      needInitFlag = true;
    }
    initDepth = props.initDepth || 2;
    this.state = {
      origin: data,
      treeData: data.concat([]),
      treeColumns: this.initColumn(props.keyColumn, props.treeColumns)
    };
    this.loadChildren = props.loadChildren;
  }

  static getDerivedStateFromProps(nextProps, prevState) {
    let data = [];
    if (needInitFlag && nextProps.treeData && nextProps.treeData.length > 0) {
      data = initData(nextProps.treeData, needInitFlag);
      needInitFlag = false;
    } else {
      data = initData(nextProps.treeData, false);
    }
    return {
      origin: data,
      treeData: data.concat([])
    };
  }

  initData(list) {
    const result = [];
    this.traversal(list, result, 0);
    return result;
  }

  traversal(firstLevels, list, depth) {
    for (let i = 0; i < firstLevels.length; i += 1) {
      const node = firstLevels[i];
      node.index = list.length;
      node.depth = depth;
      if (depth < initDepth) {
        list.push(node);
      }
      this.traversal(node.children, list, depth + 1);
    }
  }

  initColumn(key, list) {
    const render = key.render;
    key.render = function(data) {
      return (
        <div className={"key-column depth-" + data.depth} onClick={() => this.click(data)}>
          {data.children && data.children.length > 0
            ? <span>
              {foldList.indexOf(data.id) < 0
                ? <i className="el-icon-arrow-down" />
                : <i className="el-icon-arrow-right" />
              }
            </span>
            : <span><i className="empty-children" /></span>}
          <span className="key-column-name">
            {render ? render(data) : this.getValue(key.prop, data)}
          </span>
        </div>
      );
    }.bind(this);
    key.width = 600;
    list.splice(0, 0, key);
    return list;
  }

  getValue(keyProp, data) {
    const splits = keyProp.split(".");
    let origin = data;
    for (let i = 0; i < splits.length; i += 1) {
      origin = origin[splits[i]];
    }
    return origin;
  }

  click(data) {
    if (data.children && data.children.length > 0) {
      const index = foldList.indexOf(data.id);
      if (index > -1) {
        foldList.splice(index, 1);
        this.setState({ treeData: this.open(data) });
      } else {
        foldList.push(data.id);
        this.setState({ treeData: this.fold(data) });
      }
    } else if (this.loadChildren) {
      this.loadChildren(data);
    }
  }

  open(data) {
    const list = this.state.treeData;
    const index = this.getIndex(data, this.state.treeData);
    this.openFromOrigin(list, index, data);
    this.loadChildren(data);
    return list.concat([]);
  }

  openFromOrigin(list, currentIndex, data) {
    let count = 1;
    const origin = this.state.origin;
    for (let i = data.index + 1; i < origin.length; i += 1) {
      if (origin[i].depth > data.depth) {
        list.splice(currentIndex + count, 0, origin[i]);
        count += 1;
      } else {
        break;
      }
    }
  }

  fold(data) {
    const list = this.state.treeData;
    const index = this.getIndex(data, list);
    let count = 0;
    for (let i = index + 1; i < list.length; i += 1) {
      if (list[i].depth > data.depth) {
        count += 1;
      } else {
        break;
      }
    }
    list.splice(index + 1, count);
    return list.concat([]);
  }

  getIndex(data, list) {
    for (let i = 0; i < list.length; i += 1) {
      if (list[i].id === data.id) {
        return i;
      }
    }
    return -1;
  }

  tryLoadChildren(data) {
    const index = loadList.indexOf(data.id);
    if (index < 0) {
      return this.loadChildren(data);
    }
  }

  render() {
    return (
      <div className="el-tree-table">
        <Table
          style={{ width: "100%" }}
          columns={this.state.treeColumns}
          data={this.state.treeData.concat([])}
        />
      </div>
    );
  }
}
TreeTable.propTypes = {
  treeData: PropTypes.object.treeData,
  treeColumns: PropTypes.array.treeColumns,
  keyColumn: PropTypes.array.keyColumn,
  loadChildren: PropTypes.func,
  initDepth: PropTypes.initDepth
};

function initData(list, needInit) {
  const result = [];
  if (needInit) {
    initTraversal(list, result, 0, needInit);
  } else {
    traversal(list, result, 0, needInit);
  }
  return result;
}

function traversal(firstLevels, list, depth) {
  for (let i = 0; i < firstLevels.length; i += 1) {
    const node = firstLevels[i];
    node.index = list.length;
    node.depth = depth;
    list.push(node);
    if (foldList.indexOf(node.id) < 0) {
      traversal(node.children, list, depth + 1);
    }
  }
}

function initTraversal(firstLevels, list, depth) {
  if (depth === initDepth) {
    return;
  }
  for (let i = 0; i < firstLevels.length; i += 1) {
    const node = firstLevels[i];
    node.index = list.length;
    node.depth = depth;
    if (depth === initDepth - 1 && node.children && node.children.length > 0) {
      foldList.push(node.id);
    }
    list.push(node);
    if (foldList.indexOf(node.id) < 0) {
      initTraversal(node.children, list, depth + 1);
    }
  }
}