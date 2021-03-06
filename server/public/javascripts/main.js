$(function() {

  var uuid = null;

  var fadeIn = function() {
    $('.content').fadeIn(6000);
  };

  var getQuestion = function(response) {
    var url = (uuid === null) ? '/start' : ('/next/' + uuid + '/' + response);
    $.get(url, function(data) {
      if (data == 'FAIL') {
        uuid = null;
        getQuestion();
        fadeIn();
      }
      if( data.sin && data.sin.text) {
        $('#sins').append('<li>' + data.sin.text + '</li>');
        setTimeout(function() {
          priestTalk('Your sin is:' + data.sin.text, function() {
            uuid = null;
            setTimeout(function() {
              priestTalk('Do you have something else to confess?');
              setTimeout(function() {
                getQuestion();
                fadeIn();
              }, 6000);
            }, 2000);
          });
        }, 3000);
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

  var priestTalk = window.priestTalk = function(text, callback) {
    speechObj.voice = speechSynthesis.getVoices().filter(function(voice) {
      return voice.name == 'Bruce';
    })[0];
    speechObj.text = text;
    speechSynthesis.speak(speechObj);
    callback && callback();
  };


  var speechObj = new SpeechSynthesisUtterance();
  speechObj.text = 'Hello, I am sister Janet, are you here to confess?';
  speechSynthesis.speak(speechObj);
  setTimeout(function() {
    speechObj.text = 'Ok! Great! I call the father';
    speechSynthesis.speak(speechObj);

    setTimeout(function() {
      priestTalk('Hello my son, why have you come here?');
      $('#fond').fadeIn();

      setTimeout(function() {
        getQuestion();
        fadeIn();
      }, 8000);

    }, 5000);

  }, 7000);


});
