$ ->
  console.log $('#main').data 'ws-url'
  ws = new WebSocket $('#main').data 'ws-url'
  ws.onmessage = (event) ->
    message = JSON.parse event.data
    console.log message
    switch message.type
      when 'result'
        $('#testArea').append "<h3>#{message.isCollect}</h3>"
      when 'newUser'
        $('#users').append "<li>ユーザ#{message.uid}</li>"

  $('#testBtn').click (event) ->
    console.log $('#testBtn').val()
    ws.send JSON.stringify { result: $('#testBtn').val() }
