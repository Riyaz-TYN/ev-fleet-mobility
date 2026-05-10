# EV Fleet Mobility Workflow Test Sequence

1. Signup and login as a driver
   - `POST /api/auth/signup`
   - `POST /api/auth/login`
2. Create a complaint
   - `POST /api/complaints`
3. Submit a driver workflow response
   - `POST /api/workflow/user-response`
4. Assign the complaint to a manager or vendor admin
   - `PUT /api/complaints/assign`
5. Vendor accepts and updates status
   - `POST /api/workflow/vendor-response`
   - `PUT /api/complaints/status`
6. Manager decision
   - `POST /api/workflow/manager-response`
7. Resolve the complaint
   - `PUT /api/complaints/resolve`
8. Audit and reporting
   - `POST /api/audit-logs/complaint`
   - `GET /api/audit-logs/complaint/{complaintId}`
