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
        console.log message.users
        for uid, continuationCorrect of message.users
          $('#users').append "<li id=\"uid_#{uid}\" class=\"list-group-item\"></li>"
          $("#uid_#{uid}").append "ユーザ#{uid} "
          unless continuationCorrect is 0
            for i in [1..continuationCorrect]
              $("#uid_#{uid}").append "<span class=\"glyphicon glyphicon-star\" aria-hidden=\"true\"></span>"
          unless continuationCorrect is 5
            for i in [1..(5 - continuationCorrect)]
              $("#uid_#{uid}").append "<span class=\"glyphicon glyphicon-star-empty\" aria-hidden=\"true\"></span>"
      else
        console.log '[Error] unmatch message'

  $('#answer').keypress (e) ->
    if e.which is 13
      input = Number $(this).val()
      correctAnswer = $(this).data 'answer'
      isCorrect = input is correctAnswer
      ws.send JSON.stringify { result: isCorrect }
      $(this).val ''
