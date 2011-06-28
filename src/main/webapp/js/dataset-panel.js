window.onload = function () {
    var p = Raphael("dataset-panel", "100%", "100%");

    var move = function (dx, dy) {
        this.attr({cx: this.ox + dx, cy: this.oy + dy});
    },
    dragger = function () {
        this.ox = this.attr("cx");
        this.oy = this.attr("cy");
        this.animate({"fill-opacity": .2}, 100);
    },
    up = function () {
        this.animate({"fill-opacity": 0.8}, 100);
    };
    
	var datasets = new Array();
    datasets.push(p.ellipse(190, 100, 30, 30));
    datasets.push(p.ellipse(450, 100, 20, 20));
	
    for (var i=0, ii=datasets.length; i<ii; i++) {
    	datasets[i].attr({fill:"#ff0000", stroke:"#ff0000", "fill-opacity": 0.8, "stroke-width": 2, cursor: "move"});
    	datasets[i].drag(move, dragger, up);
    }
}


