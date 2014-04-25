$(function(){

  var recupQuestion = function(url){
    $.get(url, function(data){
      $('#question').html(data.question);
      $('#question').attr('data-uuid').html(data.uuid);
    } )
  };

//  recupQuestion('/start');

  $('.btn').click(function(e){
    var $this = $(this);
    var response = $this.attr('href');
    console.log(response);
    var uuid = $('#question').attr('data-uuid');
    console.log(uuid);
    //Envoi de données
    //recupQuestion('/next/'+uuid+'/'+response);
    e.preventDefault();
  })



})