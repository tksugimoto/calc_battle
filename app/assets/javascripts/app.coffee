$ ->
  ws = new WebSocket $('body').data 'ws-url'
  ws.onmessage = (event) ->
    message = JSON.parse event.data
    console.log message
    switch message.type
      when 'result'
        if message.isCorrect
          $("#users #uid_#{message.uid}").html """
            ユーザ#{message.uid}
            <span class="glyphicon glyphicon-star" aria-hidden="true"></span>
            <span class="glyphicon glyphicon-star-empty" aria-hidden="true"></span>
            <span class="glyphicon glyphicon-star-empty" aria-hidden="true"></span>
            <span class="glyphicon glyphicon-star-empty" aria-hidden="true"></span>
            <span class="glyphicon glyphicon-star-empty" aria-hidden="true"></span>
          """
        else
          $("#users #uid_#{message.uid}").html """
            ユーザ#{message.uid}
            <span class="glyphicon glyphicon-star-empty" aria-hidden="true"></span>
            <span class="glyphicon glyphicon-star-empty" aria-hidden="true"></span>
            <span class="glyphicon glyphicon-star-empty" aria-hidden="true"></span>
            <span class="glyphicon glyphicon-star-empty" aria-hidden="true"></span>
            <span class="glyphicon glyphicon-star-empty" aria-hidden="true"></span>
          """
      when 'updateUsers'
        $('#users').empty()
        for uid in message.uids
          $('#users').append "<li id=\"uid_#{uid}\" class=\"list-group-item\"></li>"
          $("#uid_#{uid}").html """
            ユーザ#{uid}
            <span class="glyphicon glyphicon-star-empty" aria-hidden="true"></span>
            <span class="glyphicon glyphicon-star-empty" aria-hidden="true"></span>
            <span class="glyphicon glyphicon-star-empty" aria-hidden="true"></span>
            <span class="glyphicon glyphicon-star-empty" aria-hidden="true"></span>
            <span class="glyphicon glyphicon-star-empty" aria-hidden="true"></span>
          """
      else
        console.log '[Error] unmatch message'

  $('#answer').keypress (e) ->
    if e.which is 13
      input = Number $(this).val()
      correctAnswer = $(this).data 'answer'
      isCorrect = input is correctAnswer
      ws.send JSON.stringify { result: isCorrect }
      $(this).val ''
