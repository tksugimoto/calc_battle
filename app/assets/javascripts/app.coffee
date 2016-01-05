$ ->
  ws = new WebSocket $('body').data 'ws-url'
  ws.onmessage = (event) ->
    message = JSON.parse event.data
    console.log message
    switch message.type
      when 'question'
        question = message.question
        $('#question').html "#{question.a} + #{question.b}"
        $('#answer').attr 'answer', question.a + question.b
      when 'updateUser'
        for uid, continuationCorrect of message.user
          $("#uid_#{uid}").empty()
          updateStar(uid, continuationCorrect)
      when 'updateUsers'
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


  $(document).on 'keypress', '#answer', (e) ->
    if e.which is 13
      input = $(this).val().trim()
      return unless input
      ws.send JSON.stringify { result: input is $(this).attr 'answer' }
      $(this).val ''
