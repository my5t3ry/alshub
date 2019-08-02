import {Injectable, Input, OnInit, Renderer2} from '@angular/core';
import {createGitgraph, TemplateName} from "@gitgraph/js";
import {Template, TemplateOptions} from '@gitgraph/core/lib/template';

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
      spacing: 80,
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
  COLORS = [
    'black',
    'red',
    'green',
    'blue'
  ];

  getColor(item) {
    if (item.color) {
      return this.COLORS[item.color.index % this.COLORS.length];
    } else {
      return 'black';
    }
  }

  drawLine(context, item) {
    context.beginPath();
    context.moveTo(item.a.x, item.a.y);
    context.lineTo(item.b.x, item.b.y);
    context.lineWidth = item.width;
    context.strokeStyle = this.getColor(item);
    context.stroke();
  }

  drawOval(context, item) {
    context.beginPath();
    var centerX = item.c.x + item.d.w / 2.0;
    var centerY = item.c.y + item.d.h / 2.0;
    context.arc(centerX, centerY, item.d.w / 2.0, 0, 2 * Math.PI, false);
    context.fillStyle = this.getColor(item);
    context.fill();
  }


  public

  drawItem(ctx, item) {
    if ('line' == item.type) {
      this.drawLine(ctx, item);
    } else if ('oval' == item.type) {
      this.drawOval(ctx, item);
    } else {
      // $log.error('[plot] Unknown item type: ', item.type);
    }
  }

  draw(container, plot, eventMethods) {
    container.childNodes.forEach(childContainer => container.removeChild(childContainer));
    const div = this.renderer.createElement('div');
    this.renderer.appendChild(container, div);
    const gitgraph = createGitgraph(div, {template: this.template});
    const master = gitgraph.branch({name: "master"});
    plot.forEach(curCommit => {
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
      master.commit(
        Object.assign({}, curCommit, this.commitOptions, eventMethods, commitStyle)
      )
    })
  }
}
