Observation.app = function(container) {
	var form = $('#deployment-form', container);
	var uploadFile = function(file) {
		var maxsize = 4 * 1024 * 1024;
		if (file.size > maxsize) {
			alert('文件太大');
			return;
		}
		if (file.type && file.type != 'application/zip'
				&& file.type != 'text/xml') {
			alert('文件类型不对,只接受zip和xml');
			return;
		}
		$.ajaxupload([file], {
					url : form.attr('action') + '/upload',
					onsuccess : function() {
						$('.reload', form).click()
					}
				});
	}
	form.on('dragover', function(e) {
				$(this).addClass('drophover');
				return false;
			}).on('dragleave', function(e) {
				$(this).removeClass('drophover');
				return false;
			}).on('drop', function(e) {
				e.preventDefault();
				$(this).removeClass('drophover');
				uploadFile(e.originalEvent.dataTransfer.files[0]);
				return true;
			});
	$('.btn.deploy', form).on('click', function(e) {
				$('<input type="file"/>').insertAfter(this).hide().change(
						function() {
							uploadFile(this.files[0]);
							$(this).remove();
						}).click();
			});

	$('.trace-process', container).each(function() {
		var t = $(this).css('position', 'relative');
		var processInstanceId = t.data('processinstanceid');
		t.html('<img alt="跟踪工作流" src="' + CONTEXT_PATH
				+ '/process/processInstance/diagram/' + processInstanceId
				+ '" style="position:absolute;">');
		var img = $('img', t)[0];
		img.onload = function() {
			var ratio = img.width / img.naturalWidth;
			$.getJSON(CONTEXT_PATH + '/process/processInstance/trace/'
							+ processInstanceId, function(data) {
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
									if (v.currentActiviti) {
										div.css({
													'border' : '2px solid red',
													'border-radius' : '12px'
												});
									}
								});
					});
		}

	});
}