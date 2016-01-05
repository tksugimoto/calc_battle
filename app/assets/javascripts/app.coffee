$ ->
  ws = new WebSocket $('body').data 'ws-url'
  ws.onmessage = (event) ->
    message = JSON.parse event.data
    console.log message
    switch message.type
      when 'updateUser'
        console.log message.user
        for uid, continuationCorrect of message.user
          $("#uid_#{uid}").empty()
          updateStar(uid, continuationCorrect)
      when 'updateUsers'
        console.log message.users
        $('#users').empty()
        for uid, continuationCorrect of message.users
          $('#users').append "<li id=\"uid_#{uid}\" class=\"list-group-item\"></li>"
          updateStar(uid, continuationCorrect)
      else
        console.log '[Error] unmatch message'

  updateStar = (uid, continuationCorrect) ->
    $("#uid_#{uid}").append "ユーザ#{uid} "
    unless continuationCorrect is 0
      for i in [1..continuationCorrect]
        $("#uid_#{uid}").append "<span class=\"glyphicon glyphicon-star\" aria-hidden=\"true\"></span>"
    unless continuationCorrect is 5
      for i in [1..(5 - continuationCorrect)]
        $("#uid_#{uid}").append "<span class=\"glyphicon glyphicon-star-empty\" aria-hidden=\"true\"></span>"


  $('#answer').keypress (e) ->
    if e.which is 13
      input = Number $(this).val().trim()
      return unless input
      correctAnswer = $(this).data 'answer'
      isCorrect = input is correctAnswer
      ws.send JSON.stringify { result: isCorrect }
      $(this).val ''
