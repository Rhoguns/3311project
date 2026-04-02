// ============================================================
// CONFIG
// ============================================================
// In Docker: nginx proxies /api --> backend:8080/api, so use relative path.
// For local dev without Docker: set window.API_BASE = 'http://localhost:8080/api' before this script.
const API = window.API_BASE || '/api';

// Current session state
const session = {
  role: 'client',
  page: 'dashboard',
  clientId: 'cl001',
  consultantId: 'c001',
};

// ============================================================
// CONSULTANT PAGES
// ============================================================
async function renderMySchedule(el) {
  const bookings = await apiSafe(`/bookings/consultant/${session.consultantId}`)||[];
  el.innerHTML=`<div class="card">
    ${tableOf(['ID','Client','Service','Date','Amount','State','Actions'],
      bookings.filter(b=>!['CANCELLED','REJECTED'].includes(b.state)).map(b=>`<tr>
        <td class="td-mono">#${b.id}</td><td>${b.clientName}</td><td>${b.serviceName}</td>
        <td class="td-mono text-sm">${fmtDate(b.slotStart)}</td>
        <td class="td-mono">${fmtMoney(b.amount)}</td>
        <td>${badge(b.state)}</td>
        <td class="flex gap-2">
          ${b.state==='REQUESTED'?`<button class="btn btn-xs btn-success" onclick="acceptBooking('${b.id}')">Accept</button><button class="btn btn-xs btn-danger" onclick="rejectBooking('${b.id}')">Reject</button>`:''}
          ${b.state==='PAID'?`<button class="btn btn-xs btn-success" onclick="completeBooking('${b.id}')">Complete</button>`:''}
          <button class="btn btn-xs btn-ghost" onclick="viewBooking('${b.id}')">Detail</button>
        </td>
      </tr>`),
      'No bookings'
    )}
  </div>`;
}

async function renderIncoming(el) {
  const bookings = await apiSafe(`/bookings/consultant/${session.consultantId}`)||[];
  const req = bookings.filter(b=>b.state==='REQUESTED');
  el.innerHTML=`<div class="card">
    <div class="card-header"><div class="card-title">Pending Requests (${req.length})</div></div>
    ${tableOf(['Client','Service','Date','Amount','Actions'],
      req.map(b=>`<tr>
        <td>${b.clientName}</td><td>${b.serviceName}</td>
        <td class="td-mono text-sm">${fmtDate(b.slotStart)}</td>
        <td class="td-mono font-bold">${fmtMoney(b.amount)}</td>
        <td class="flex gap-2">
          <button class="btn btn-xs btn-success" onclick="acceptBooking('${b.id}')">✓ Accept</button>
          <button class="btn btn-xs btn-danger" onclick="rejectBooking('${b.id}')">✕ Reject</button>
          <button class="btn btn-xs btn-ghost" onclick="viewBooking('${b.id}')">Detail</button>
        </td>
      </tr>`),
      'No pending requests — all caught up!'
    )}
  </div>`;
}

async function renderManageBookings(el) {
  const bookings = await apiSafe(`/bookings/consultant/${session.consultantId}`)||[];
  el.innerHTML=`<div class="card">
    ${tableOf(['ID','Client','Service','Date','Amount','State','Actions'],
      bookings.map(b=>`<tr>
        <td class="td-mono">#${b.id}</td><td>${b.clientName}</td><td>${b.serviceName}</td>
        <td class="td-mono text-sm">${fmtDate(b.slotStart)}</td>
        <td class="td-mono">${fmtMoney(b.amount)}</td>
        <td>${badge(b.state)}</td>
        <td class="flex gap-2">
          ${b.state==='REQUESTED'?`<button class="btn btn-xs btn-success" onclick="acceptBooking('${b.id}')">Accept</button><button class="btn btn-xs btn-danger" onclick="rejectBooking('${b.id}')">Reject</button>`:''}
          ${b.state==='PAID'?`<button class="btn btn-xs btn-success" onclick="completeBooking('${b.id}')">Done</button>`:''}
          <button class="btn btn-xs btn-ghost" onclick="viewBooking('${b.id}')">Detail</button>
        </td>
      </tr>`),
      'No bookings'
    )}
  </div>`;
}

async function renderAvailability(el) {
  const con   = await apiSafe(`/consultants/${session.consultantId}`);
  const slots = con?.slots||[];
  el.innerHTML=`
  <div class="card">
    <div class="card-header"><div class="card-title">My Availability Slots</div>
      <button class="btn btn-sm btn-accent" onclick="openAddSlotModal()">＋ Add Slot</button></div>
    ${tableOf(['Slot ID','Start','End','Status','Actions'],
      slots.map(s=>`<tr>
        <td class="td-mono">${s.id}</td>
        <td class="td-mono text-sm">${fmtDate(s.start)}</td>
        <td class="td-mono text-sm">${fmtDate(s.end)}</td>
        <td>${badge(s.available?'AVAILABLE':'BOOKED')}</td>
        <td>${s.available?`<button class="btn btn-xs btn-danger" onclick="removeSlot('${s.id}')">Remove</button>`:''}</td>
      </tr>`),
      'No slots added yet'
    )}
  </div>`;
}

function openAddSlotModal() {
  openModal('Add Availability Slot',`
    <div class="form-grid">
      <div class="form-group"><label>Start</label><input type="datetime-local" id="slot-start"/></div>
      <div class="form-group"><label>End</label><input type="datetime-local" id="slot-end"/></div>
    </div>`,
    `<button class="btn btn-ghost" onclick="closeModal()">Cancel</button>
     <button class="btn btn-accent" onclick="addSlot()">Add Slot</button>`
  );
}

async function addSlot() {
  const start = document.getElementById('slot-start').value;
  const end   = document.getElementById('slot-end').value;
  if (!start||!end) { toast('Fill both fields','error'); return; }
  setLoading(true);
  const r = await apiSafe(`/consultants/${session.consultantId}/slots`,'POST',{start,end});
  setLoading(false);
  if (r) { closeModal(); toast('Slot added','success'); navigate('availability'); }
}

async function removeSlot(slotId) {
  setLoading(true);
  const r = await apiSafe(`/consultants/${session.consultantId}/slots/${slotId}`,'DELETE');
  setLoading(false);
  if (r) { toast('Slot removed','info'); navigate('availability'); }
}
