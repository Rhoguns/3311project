// CONFIG
const API = window.API_BASE || '/api';

// Current session state
const session = {
  role: 'client',
  page: 'dashboard',
  clientId: 'cl001',
  consultantId: 'c001',
};


// CONSULTANT PAGES
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



// ADMIN PAGES

async function renderPolicies(el) {
  const p = await apiSafe('/admin/policies')||{};
  el.innerHTML=`
  <div class="section-header">
    <div><div class="section-title">System Policies</div>
    <div class="section-sub">Command Pattern — configure platform-wide rules</div></div>
  </div>
  <div class="policy-grid">
    <div class="policy-card">
      <div class="policy-edit"><button class="btn btn-sm btn-ghost" onclick="editCancelPolicy(${p.cancellationHours})">Edit</button></div>
      <div class="policy-icon">⏱</div><div class="policy-name">Cancellation Policy</div>
      <div class="policy-value">${p.cancellationHours}h</div>
      <div class="policy-desc">Min hours before session start to cancel</div>
    </div>
    <div class="policy-card">
      <div class="policy-edit"><button class="btn btn-sm btn-ghost" onclick="editPricePolicy(${p.priceMin},${p.priceMax})">Edit</button></div>
      <div class="policy-icon">💵</div><div class="policy-name">Pricing Policy</div>
      <div class="policy-value">${fmtMoney(p.priceMin||0)} – ${fmtMoney(p.priceMax||1000)}</div>
      <div class="policy-desc">Allowed service price range</div>
    </div>
    <div class="policy-card">
      <div class="policy-edit"><button class="btn btn-sm btn-ghost" onclick="toggleNotif(${!p.notificationsEnabled})">${p.notificationsEnabled?'Disable':'Enable'}</button></div>
      <div class="policy-icon">🔔</div><div class="policy-name">Notifications</div>
      <div class="policy-value" style="font-size:18px;color:${p.notificationsEnabled?'#27ae60':'#c0392b'}">${p.notificationsEnabled?'✓ Enabled':'✕ Disabled'}</div>
      <div class="policy-desc">System-wide notification toggle</div>
    </div>
    <div class="policy-card">
      <div class="policy-icon">↩</div><div class="policy-name">Refund Policy</div>
      <div class="policy-value" style="font-size:18px">${p.refundPolicy}</div>
      <div class="policy-desc">Payment state eligible for refund</div>
    </div>
  </div>`;
}

function editCancelPolicy(cur) {
  openModal('Cancellation Policy',`<div class="form-group"><label>Minimum hours before session</label>
    <input type="number" id="cp-hrs" value="${cur}" min="0"/></div>`,
    `<button class="btn btn-ghost" onclick="closeModal()">Cancel</button>
     <button class="btn btn-accent" onclick="saveCancelPolicy()">Save</button>`);
}
async function saveCancelPolicy() {
  const h = parseInt(document.getElementById('cp-hrs').value);
  if (isNaN(h)||h<0) { toast('Invalid','error'); return; }
  setLoading(true);
  const r = await apiSafe('/admin/policies/cancellation','PUT',{hours:h});
  setLoading(false);
  if (r) { closeModal(); toast('Cancellation policy updated','success'); navigate('policies'); }
}

function editPricePolicy(mn, mx) {
  openModal('Pricing Policy',`<div class="form-grid">
    <div class="form-group"><label>Min Price ($)</label><input type="number" id="pp-min" value="${mn}"/></div>
    <div class="form-group"><label>Max Price ($)</label><input type="number" id="pp-max" value="${mx}"/></div>
  </div>`,
    `<button class="btn btn-ghost" onclick="closeModal()">Cancel</button>
     <button class="btn btn-accent" onclick="savePricePolicy()">Save</button>`);
}
async function savePricePolicy() {
  const mn = parseFloat(document.getElementById('pp-min').value);
  const mx = parseFloat(document.getElementById('pp-max').value);
  if (isNaN(mn)||isNaN(mx)||mx<mn) { toast('Invalid range','error'); return; }
  setLoading(true);
  const r = await apiSafe('/admin/policies/pricing','PUT',{min:mn,max:mx});
  setLoading(false);
  if (r) { closeModal(); toast('Pricing policy updated','success'); navigate('policies'); }
}

async function toggleNotif(val) {
  setLoading(true);
  await apiSafe('/admin/policies/notifications','PUT',{enabled:val});
  setLoading(false);
  toast(`Notifications ${val?'enabled':'disabled'}`,'info');
  navigate('policies');
}

async function renderAllConsultants(el) {
  const cons = await apiSafe('/consultants')||[];
  el.innerHTML=`<div class="card">
    ${tableOf(['Name','Email','Status','Slots','Actions'],
      cons.map(c=>`<tr>
        <td>${c.name}</td><td class="td-mono text-sm">${c.email}</td>
        <td>${badge(c.status)}</td><td class="td-mono">${c.slots?.length||0}</td>
        <td class="flex gap-2">
          ${c.status==='PENDING'?`<button class="btn btn-xs btn-success" onclick="approveConsultant('${c.id}')">Approve</button>
            <button class="btn btn-xs btn-danger" onclick="rejectConsultant('${c.id}')">Reject</button>`:''}
          ${c.status==='APPROVED'?`<span class="text-dim text-sm">Active</span>`:''}
          ${c.status==='REJECTED'?`<span class="text-dim text-sm">Rejected</span>`:''}
        </td>
      </tr>`)
    )}
  </div>`;
}

async function renderAllBookings(el) {
  const bookings = await apiSafe('/bookings')||[];
  el.innerHTML=`<div class="card">
    ${tableOf(['ID','Client','Consultant','Service','Date','Amount','State'],
      bookings.map(b=>`<tr>
        <td class="td-mono">#${b.id}</td>
        <td>${b.clientName}</td><td>${b.consultantName}</td><td>${b.serviceName}</td>
        <td class="td-mono text-sm">${fmtDate(b.slotStart)}</td>
        <td class="td-mono font-bold">${fmtMoney(b.amount)}</td>
        <td>${badge(b.state)}</td>
      </tr>`),
      'No bookings'
    )}
  </div>`;
}

async function renderAllPayments(el) {
  const [clients] = await Promise.all([apiSafe('/clients')||[]]);
  let allPayments = [];
  for (const c of clients) {
    const p = await apiSafe(`/payments/client/${c.id}`)||[];
    allPayments = allPayments.concat(p);
  }
  const total = allPayments.filter(p=>p.state==='SUCCESSFUL').reduce((a,p)=>a+p.amount,0);
  el.innerHTML=`
  <div class="stats-grid" style="margin-bottom:16px">
    <div class="stat-card"><div class="stat-label">Total Revenue</div><div class="stat-value">${fmtMoney(total)}</div></div>
    <div class="stat-card"><div class="stat-label">Successful</div><div class="stat-value">${allPayments.filter(p=>p.state==='SUCCESSFUL').length}</div></div>
    <div class="stat-card"><div class="stat-label">Refunded</div><div class="stat-value">${allPayments.filter(p=>p.state==='REFUNDED').length}</div></div>
    <div class="stat-card"><div class="stat-label">Total Txns</div><div class="stat-value">${allPayments.length}</div></div>
  </div>
  <div class="card">
    ${tableOf(['Txn ID','Booking','Client','Amount','Method','State','Date'],
      allPayments.map(p=>`<tr>
        <td class="td-mono">${p.transactionId}</td>
        <td class="td-mono">#${p.bookingId}</td>
        <td>${p.clientId}</td>
        <td class="td-mono font-bold">${fmtMoney(p.amount)}</td>
        <td>${p.method.replace('_',' ')}</td>
        <td>${badge(p.state)}</td>
        <td class="td-mono text-sm">${fmtDate(p.createdAt)}</td>
      </tr>`),
      'No payments yet'
    )}
  </div>`;
}

// SHARED ACTIONS
async function viewBooking(id) {
  const b = await apiSafe(`/bookings/${id}`);
  if (!b) return;
  openModal(`Booking #${b.id}`,`
    ${bookingTimeline(b.state)}
    <div class="divider"></div>
    <div style="display:grid;grid-template-columns:1fr 1fr;gap:12px;font-size:13px">
      <div><span class="text-dim">Client:</span><br><strong>${b.clientName}</strong></div>
      <div><span class="text-dim">Consultant:</span><br><strong>${b.consultantName}</strong></div>
      <div><span class="text-dim">Service:</span><br><strong>${b.serviceName}</strong></div>
      <div><span class="text-dim">Amount:</span><br><strong style="font-size:20px;color:#c0392b">${fmtMoney(b.amount)}</strong></div>
      <div><span class="text-dim">Session:</span><br><strong class="td-mono">${fmtDate(b.slotStart)}</strong></div>
      <div><span class="text-dim">Booked:</span><br><strong class="td-mono">${fmtDate(b.createdAt)}</strong></div>
    </div>`,
    `<button class="btn btn-ghost" onclick="closeModal()">Close</button>
     ${b.state==='PENDING_PAYMENT'?`<button class="btn btn-accent" onclick="closeModal();openPayModal('${b.id}')">Pay Now</button>`:''}
     ${['REQUESTED','CONFIRMED'].includes(b.state)?`<button class="btn btn-danger" onclick="closeModal();cancelBooking('${b.id}')">Cancel</button>`:''}`
  );
}

async function cancelBooking(id) {
  setLoading(true);
  const r = await apiSafe(`/bookings/${id}/cancel`,'PUT');
  setLoading(false);
  if (r) { toast('Booking cancelled','info'); navigate(session.page); }
}

async function acceptBooking(id) {
  setLoading(true);
  const r = await apiSafe(`/bookings/${id}/accept`,'PUT');
  setLoading(false);
  if (r) { toast('Booking accepted — awaiting payment','success'); navigate(session.page); }
}

async function rejectBooking(id) {
  setLoading(true);
  const r = await apiSafe(`/bookings/${id}/reject`,'PUT');
  setLoading(false);
  if (r) { toast('Booking rejected','info'); navigate(session.page); }
}

async function completeBooking(id) {
  setLoading(true);
  const r = await apiSafe(`/bookings/${id}/complete`,'PUT');
  setLoading(false);
  if (r) { toast('Booking completed!','success'); navigate(session.page); }
}

async function approveConsultant(id) {
  setLoading(true);
  const r = await apiSafe(`/consultants/${id}/approve`,'PUT');
  setLoading(false);
  if (r) { toast('Consultant approved','success'); navigate(session.page); }
}

async function rejectConsultant(id) {
  setLoading(true);
  const r = await apiSafe(`/consultants/${id}/reject`,'PUT');
  setLoading(false);
  if (r) { toast('Consultant rejected','info'); navigate(session.page); }
}

// TABLE FILTERS
function filterTbl(input, tblId) {
  const q = input.value.toLowerCase();
  const t = document.getElementById(tblId)||input.closest('.card')?.querySelector('table');
  if (!t) return;
  t.querySelectorAll('tbody tr').forEach(tr=>{
    tr.style.display = tr.textContent.toLowerCase().includes(q) ? '' : 'none';
  });
}

function filterState(sel, tblId) {
  const v = sel.value;
  const t = document.getElementById(tblId)||sel.closest('.filter-bar')?.nextElementSibling?.querySelector('table');
  if (!t) return;
  t.querySelectorAll('tbody tr').forEach(tr=>{
    tr.style.display = (!v || tr.dataset.state===v) ? '' : 'none';
  });
}

// INIT
renderNav();
navigate('dashboard');

