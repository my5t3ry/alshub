import {ElementRef, Injectable, Input, OnInit, Renderer2} from '@angular/core';
import {createGitgraph, Orientation, TemplateName} from "@gitgraph/js";
import {Template, TemplateOptions} from '@gitgraph/core/lib/template';
import {GitgraphUserApi} from "@gitgraph/core";

@Injectable({
  providedIn: 'root'
})
export class GitPlotService implements OnInit {
  public renderer: Renderer2;


  ngOnInit(): void {
  }

  constructor() {
  }

  template = new Template({
    colors: ["#02978e", "#008fb5", "#47f11a"],
    branch: {
      lineWidth: 10,
      spacing: 50,
      color: "#282528"
    },
    commit: {
      spacing: 70,
      dot: {
        size: 20,
        color: "#1cc732"
      },
      message: {
        font: "normal 10pt Arial",
        color: "#232721"
      },
    },
  });

  private commitOptions = {
    style: this.template,
  }

  draw(container, branches, eventMethods) {
    container.childNodes.forEach(childContainer => container.removeChild(childContainer));
    const div:HTMLElement = this.renderer.createElement('div');
    div.classList.add("col-12");
    div.classList.add("graph-container");
    this.renderer.appendChild(container, div);
    const gitgraph  = createGitgraph(div, {template: this.template, orientation: Orientation.VerticalReverse});
    branches.forEach(curBranch => {
      const branch = gitgraph.branch({name: curBranch.subject});
      curBranch.commits.forEach(curCommit => {
        let color = "#1d93f3";
        if (curCommit.checkedOut) {
          color = "#1cc732";
        }
        let commitStyle = {
          style: {
            spacing: 80,
            dot: {
              size: 20,
              color: color
            }
          }
        };
        branch.commit(
          Object.assign({}, curCommit, this.commitOptions, eventMethods, commitStyle)
        )
      })
    })
  }
}
