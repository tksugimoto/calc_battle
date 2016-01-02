$ ->
  console.log $('#main').data 'ws-url'
  ws = new WebSocket $('#main').data 'ws-url'
  ws.onmessage = (event) ->
    message = JSON.parse event.data
    switch message.type
      when 'message'
        console.log message
        $('#testArea').append "<h3>#{message.isCollect}</h3>"
      else
        console.log message

  $("#testBtn").click (event) ->
    console.log $("#testBtn").val
    ws.send JSON.stringify(msg: $("#testBtn").val)
