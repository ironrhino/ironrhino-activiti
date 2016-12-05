Observation.process = function(container) {
	$('.diagram', container).each(function() {
		var t = $(this).css('position', 'relative');
		var pid = t.data('pid');
		var entity = t.hasClass('processDefinition')
				? 'processDefinition'
				: 'processInstance';
		t.html('<img alt="跟踪工作流" src="' + CONTEXT_PATH + '/process/' + entity
				+ '/diagram/' + pid + '" style="position:absolute;">');
		var img = $('img', t)[0];
		img.onload = function() {
			$(img).closest('.ui-dialog-content').css({
						'height' : 'auto'
					});
			t.height(img.height + 50);
			var ratio = img.width / img.naturalWidth;
			$.getJSON(CONTEXT_PATH + '/process/' + entity + '/trace/' + pid,
					function(data) {
						$.each(data, function(i, v) {
									var div = $('<div/>').css({
												position : 'absolute',
												left : (v.x * ratio - 1),
												top : (v.y * ratio - 1),
												width : (v.width * ratio - 2),
												height : (v.height * ratio - 2)
											}).appendTo(t);
									var title = [];
									for (var key in v.vars) {
										var value = v.vars[key];
										if (value) {
											title.push(key + ': ' + value);
										}
									}
									div.attr('title', title.join('\n'));
									if (v.current) {
										var radius = v['border-radius'] || '12';
										div.css({
													'border' : '2px solid red',
													'border-radius' : radius
															+ 'px'
												});
									}
								});
					});
		}

	});
	$('button.toggle-control-group', container).click(function(e) {
		$('.control-group.' + $(this).data('groupclass'),
				$(this).closest('form')).toggle();
	});
	$('a.deleteRow', container).each(function() {
				this.onprepare = function() {
					return confirm(MessageBundle.get('confirm.action'));
				}
				this.onsuccess = function() {
					$(this).closest('tr').remove();
				}
			});
}