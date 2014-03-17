Observation.deploy = function(container) {
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

}