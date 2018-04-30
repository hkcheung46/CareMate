# CareMate
A family care activities coordination android mobile application powered by Firebase platform

<b>Functionalities of this application:</b>

<ol>
 <li>User registration, sign in and out control </li>
  <ul>
    <li>Handle the user registration & sign in out flow</li>
    <li>Powered by Firebase</li>
  </ul>
 <li>User group creation and maintenance</li>
 <ul>
    <li>Allow the creation of elderly care group, each group is for holding all care-givers handling care-activities of one elder</li>
    <li>Allow new group member invitations with whatsapp like mechanism, only allow add registered users inside their contact list</li>
    <li>Push notification to user when they receive new invitations</li>
    <li>User can accept/reject group joining invitations</li>
    <li>Support quit group</li>
  </ul>
 <li>To-do item creation and operation (to-do item basis discussion inclusive)</li>
  <ul>
    <li>Support the creation and assignment of to-do item inside a user group</li>
    <li>Support two kinds of to-do items:</li>
      <ul>
        <li>Tasks - To do item that only have deadlines, usually one person to handle is good enough</li>
        <li>Events - Usually have start and end dates, usually need many people to join, one person attend doesn't mean others don't need to</li>
      </ul>
    <li>Maintain task owner list/ event attendee list, with task status to visualize the progress of the to-do item</li>
    <li>Group basis/Personal basis to-do item list to keep tracking of what is pending a users decision/involvement</li>
    <li>Push notification to group member when there are new to-do item created inside the user group</li>
    <li>To-do item basis chatroom to isolate casual chatting from to-do item related discussion</li>
  </ul>
 <li>Polling creation and operation</li>
 <ul>
    <li>Support creation of both single vote and multiple vote polling inside a user group</li>
    <li>Support modify vote</li>
    <li>With polling status to keep track of which polling is still accepting votes</li>
    <li>Pie Chart to visualize polling result</li>
  </ul>
 <li>Data logger linkage and dashboard</li>
  <ul>
    <li>Support linkage to data-logger</li>
    <li>Support display humidity and temperature sensor's reading</li>
  </ul>
</ol>
