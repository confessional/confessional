$(function() {

  var uuid = null;

  $('.content').fadeIn(8000);

  var getQuestion = function(response) {
    var url = (uuid === null) ? '/start' : ('/next/' + uuid + '/' + response);
    $.get(url, function(data) {
      if (data.text) {
        $('#sins').append('<li>' + data.text + '</li>');
        uuid = null;
        getQuestion();
      } else {
        uuid = data.uuid;
        $('#question').html(data.question.text);
      }
    });
  };

  $('.btn').click(function() {
    getQuestion($(this).data('val'));
  });

  getQuestion();

});
