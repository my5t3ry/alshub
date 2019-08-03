import * as React from "react";
import Tree from 'react-d3-tree';

export class TreeComponent extends React.Component<{ data: any, onClickHandler: any }, {}> {
  render() {
    return (
      <div id="treeWrapper" style={{width: '100%', height: '1200px'}}>
        <Tree data={this.props.data} orientation="horizontal" onClick={this.props.onClickHandler} separation={{siblings: 1, nonSiblings: 1}} />
      </div>
    );
  }
}
