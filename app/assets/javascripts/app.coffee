$ ->
  console.log $('body').data 'ws-url'
  ws = new WebSocket $('body').data 'ws-url'
  ws.onmessage = (event) ->
    message = JSON.parse event.data
    console.log message
    switch message.type
      when 'result'
        $('#result').html "<h3>#{message.isCorrect}</h3>"
      when 'newUser'
        $('#users').append "<li id=\"uid_#{message.uid}\" class=\"list-group-item\">ユーザ#{message.uid}</li>"
      when 'destroyUser'
        $("#users #uid_#{message.uid}").remove

  $('#answer').keypress (e) ->
    if e.which is 13
      input = Number $(this).val()
      correctAnswer = $(this).data 'answer'
      isCorrect = input is correctAnswer
      ws.send JSON.stringify { result: isCorrect }
      $(this).val ''
