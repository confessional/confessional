$(function() {

  var uuid = null;

  var fadeIn = function() {
    $('.content').fadeIn(8000);
  };
  fadeIn();

  var getQuestion = function(response) {
    var url = (uuid === null) ? '/start' : ('/next/' + uuid + '/' + response);
    $.get(url, function(data) {
      if (data == 'FAIL') {
        uuid = null;
        getQuestion();
        fadeIn();
      }
      if (data.text) {
        $('#sins').append('<li>' + data.text + '</li>');
        uuid = null;
        getQuestion();
        fadeIn();
      } else {
        uuid = data.uuid;
        $('#question').html(data.question.text);
        priestTalk(data.question.text);
      }
    });
  };

  $('.btn').click(function() {
    getQuestion($(this).data('val'));
  });

  var priestTalk = window.priestTalk = function(text) {
    speechObj.voice = speechSynthesis.getVoices().filter(function(voice) {
      return voice.name == 'Bruce';
    })[0];
    speechObj.text = text;
    speechSynthesis.speak(speechObj);
  };


  var speechObj = new SpeechSynthesisUtterance();
  speechObj.text = 'Hello, I am sister Janet, are you here to confess?';
  speechSynthesis.speak(speechObj);
  setTimeout(function() {
    speechObj.text = 'Ok! Great! I call the father';
    speechSynthesis.speak(speechObj);

    getQuestion();
  }, 5000);


});