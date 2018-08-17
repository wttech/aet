import React, {Component} from 'react';

export default class StaticBlock extends Component {
  constructor(props) {
    super(props);

    this.state = {
      collectorsExpanded: true,
      comparatorsExpanded: true,
      urlsExpanded: true
    }

    this.handleExpanding = this.handleExpanding.bind(this);
  }

  toggleExpanding(lists, arrow, isExpanded) {
    lists.forEach((listItem) => {
      if(isExpanded) {
        listItem.style.height = 0;
        arrow.style.transform = "rotate(90deg)";
      } else {
        listItem.style.height = "100%";
        arrow.style.transform = "rotate(0deg)";
      }
    });
  }

  handleExpanding() {
    if(this.props.expandable) {
      const category = this.props.value.toString().toLowerCase();
      if(category === "collectors") {
        const modifiers = document.querySelector(".modifiers-list");
        const collectors = document.querySelector(".collectors-list");
        const arrow = document.querySelector("#Collectors-start>.block-arrow");
        this.toggleExpanding([modifiers, collectors], arrow, this.state.collectorsExpanded);
        this.setState({collectorsExpanded: !this.state.collectorsExpanded});
      } else if (category === "comparators") {
        const comparators = document.querySelector(".comparators-list");
        const arrow = document.querySelector("#Comparators-start>.block-arrow");
        this.toggleExpanding([comparators], arrow, this.state.collectorsExpanded);
        this.setState({collectorsExpanded: !this.state.collectorsExpanded});
      } else {
        const urls = document.querySelector(".urls-list");
        const arrow = document.querySelector("#URLs-start>.block-arrow");
        this.toggleExpanding([urls], arrow, this.state.collectorsExpanded);
        this.setState({collectorsExpanded: !this.state.collectorsExpanded});
      }
    }
  }

  render() {
    if(this.props.type === "start") {
      return (
        <div className="block static start" id={this.props.value + "-start"} onClick={() => this.handleExpanding()}>
          {this.props.value} {this.props.type === "start" ? (<i className="fas fa-angle-right block-arrow"></i>) : null}
        </div>
      )
    } else if (this.props.type === "end") {
      return (
        <div className="block static end">
          {this.props.value}
        </div>
      )
    } else if(this.props.type === "open") {
      return (
        <div className="block static nested">{this.props.value}</div>
      )
    } else {
      return null;
    }
  }
}