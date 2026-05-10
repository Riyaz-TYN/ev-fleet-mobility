import json
import uuid

base_url = '{{baseUrl}}'

collection = {
    'info': {
        '_postman_id': str(uuid.uuid4()),
        'name': 'EV Fleet Mobility Complaint Resolution System',
        'schema': 'https://schema.getpostman.com/json/collection/v2.1.0/collection.json',
        'description': 'Enterprise Postman collection for EV Fleet Mobility Complaint Resolution System. All requests use actual implemented endpoints, DTO payloads, and RBAC requirements from the repository.'
    },
    'item': [],
    'variable': [
        {'key': 'baseUrl', 'value': 'http://localhost:8080', 'type': 'string'},
        {'key': 'driverToken', 'value': '', 'type': 'string'},
        {'key': 'vendorToken', 'value': '', 'type': 'string'},
        {'key': 'managerToken', 'value': '', 'type': 'string'},
        {'key': 'adminToken', 'value': '', 'type': 'string'},
        {'key': 'superAdminToken', 'value': '', 'type': 'string'},
        {'key': 'refreshToken', 'value': '', 'type': 'string'},
        {'key': 'invalidJwt', 'value': 'Bearer invalid.token.example', 'type': 'string'},
        {'key': 'complaintId', 'value': '100', 'type': 'string'},
        {'key': 'taskId', 'value': 'task-123', 'type': 'string'},
        {'key': 'queryId', 'value': '1', 'type': 'string'},
        {'key': 'vendorId', 'value': '200', 'type': 'string'},
        {'key': 'vehicleId', 'value': '300', 'type': 'string'},
        {'key': 'targetUserId', 'value': '400', 'type': 'string'},
    ]
}


def response_examples(name, success_body, success_code=200, error_body=None, error_code=400):
    examples = [
        {
            'name': f'{name} - Success',
            'originalRequest': {'method': '', 'header': [], 'url': {'raw': ''}},
            'status': 'OK',
            'code': success_code,
            'body': json.dumps(success_body, indent=2)
        }
    ]
    if error_body is not None:
        examples.append(
            {
                'name': f'{name} - Error',
                'originalRequest': {'method': '', 'header': [], 'url': {'raw': ''}},
                'status': 'Error',
                'code': error_code,
                'body': json.dumps(error_body, indent=2)
            }
        )
    return examples


def request_item(name, method, url, headers, body, description, responses):
    req = {
        'name': name,
        'request': {
            'method': method,
            'header': headers,
            'url': {
                'raw': url,
                'host': ['{{baseUrl}}'],
                'path': url.replace('{{baseUrl}}/', '').split('/')
            },
            'description': description
        },
        'response': responses
    }
    if body is not None and method in ['POST', 'PUT', 'PATCH']:
        req['request']['body'] = {
            'mode': 'raw',
            'raw': json.dumps(body, indent=2),
            'options': {'raw': {'language': 'json'}}
        }
    return req


def folder(name, items):
    return {'name': name, 'item': items}

# Add all request folders and items here
collection['item'].append(folder('Auth', [
    request_item(
        'Signup User', 'POST', base_url + '/api/auth/signup',
        [{'key': 'Content-Type', 'value': 'application/json'}],
        {
            'fullName': 'Driver One',
            'email': 'driver@example.com',
            'password': 'DriverPass1!',
            'role': 'DRIVER',
            'userType': 'INDIVIDUAL',
            'phoneNumber': '+911234567890',
            'countryCode': '+91',
            'gender': 'MALE',
            'companyName': 'DriverCorp'
        },
        'RBAC: Public signup endpoint. Can create DRIVER / VENDOR / MANAGER / ADMIN / SUPER_ADMIN users.\nWorkflow impact: register new user for complaint/resolution onboarding.',
        response_examples(
            'Signup',
            {'message': 'Signup successful', 'data': {'username': 'Driver One', 'email': 'driver@example.com'}},
            200,
            {'status': 409, 'error': 'Duplicate Entry', 'message': 'Email is already registered'},
            409
        )
    ),
    request_item(
        'Login User', 'POST', base_url + '/api/auth/login',
        [{'key': 'Content-Type', 'value': 'application/json'}],
        {'email': 'driver@example.com', 'password': 'DriverPass1!'},
        'RBAC: Public login endpoint. Returns accessToken and refreshToken for Bearer auth.\nWorkflow impact: authenticate user to perform complaint and workflow actions.',
        response_examples(
            'Login',
            {'message': 'Login successful', 'data': {'accessToken': '{{driverToken}}', 'refreshToken': '{{refreshToken}}'}},
            200,
            {'status': 401, 'error': 'Invalid Credentials', 'message': 'Invalid password'},
            401
        )
    ),
    request_item(
        'Refresh Token', 'POST', base_url + '/api/auth/refresh',
        [{'key': 'Content-Type', 'value': 'application/json'}],
        {'refreshToken': '{{refreshToken}}'},
        'RBAC: Public refresh endpoint. Uses refresh token to issue a new accessToken.\nWorkflow impact: maintain authenticated session for long-running complaint resolution.',
        response_examples(
            'Refresh',
            {'message': 'Token refreshed', 'data': {'accessToken': '{{driverToken}}', 'refreshToken': '{{refreshToken}}'}},
            200,
            {'status': 400, 'error': 'Invalid Token', 'message': 'Invalid token type. Refresh token required.'},
            400
        )
    ),
    request_item(
        'Logout User', 'POST', base_url + '/api/auth/logout',
        [{'key': 'Content-Type', 'value': 'application/json'}],
        None,
        'RBAC: Public logout endpoint. Clears client-side tokens.\nWorkflow impact: end user session.',
        response_examples(
            'Logout',
            {'message': 'Logged out successfully', 'data': 'Clear tokens from client'},
            200,
            {'status': 401, 'error': 'Invalid Token', 'message': 'Token is malformed or tampered.'},
            401
        )
    )
]))

collection['item'].append(folder('Driver', [
    request_item(
        'Create Complaint', 'POST', base_url + '/api/complaints',
        [{'key': 'Content-Type', 'value': 'application/json'}, {'key': 'Authorization', 'value': 'Bearer {{driverToken}}'}],
        {
            'complaintData': {
                'issueCategory': 'Battery Failure',
                'issueDescription': 'Battery drains within 10 miles of charge.',
                'location': 'Warehouse district',
                'severity': 'HIGH'
            },
            'latitude': 12.9716,
            'longitude': 77.5946
        },
        'RBAC: DRIVER only. Creates a complaint and starts the complaint workflow in Camunda.\nWorkflow impact: initiates AI assessment, vendor assignment, and escalation checks.',
        response_examples(
            'Create Complaint',
            {'message': 'Complaint saved & workflow started', 'payloadSentToAI': {'complaintData': {'issueCategory': 'Battery Failure', 'issueDescription': 'Battery drains within 10 miles of charge.', 'location': 'Warehouse district', 'severity': 'HIGH'}, 'latitude': 12.9716, 'longitude': 77.5946}},
            200,
            {'status': 500, 'error': 'Internal Server Error', 'message': 'No vehicle found for this user'},
            500
        )
    ),
    request_item(
        'Get My Complaints', 'GET', base_url + '/api/complaints',
        [{'key': 'Authorization', 'value': 'Bearer {{driverToken}}'}],
        None,
        'RBAC: DRIVER + VENDOR_ADMIN + MANAGER + ADMIN + SUPER_ADMIN. Drivers retrieve their own complaints.\nWorkflow impact: track complaint status through AI and vendor workflow.',
        response_examples(
            'Get Complaints',
            [{'id': 101, 'status': 'IN_PROGRESS', 'issueCategory': 'Battery Failure', 'priority': 'LOW', 'assignedTeam': None, 'data': '{"issueCategory":"Battery Failure","issueDescription":"Battery drains...","location":"Warehouse district","severity":"HIGH"}', 'createdAt': '2026-05-09T12:00:00', 'customerId': '1', 'vehicleId': '300', 'vendorId': None, 'latitude': 12.9716, 'longitude': 77.5946, 'escalationReason': None}],
            200,
            {'status': 401, 'error': 'Invalid Token', 'message': 'Token is malformed or tampered.'},
            401
        )
    ),
    request_item(
        'Get Complaint Details', 'POST', base_url + '/api/complaints/details',
        [{'key': 'Content-Type', 'value': 'application/json'}, {'key': 'Authorization', 'value': 'Bearer {{driverToken}}'}],
        {'complaintId': 100},
        'RBAC: DRIVER + VENDOR_ADMIN + MANAGER + ADMIN + SUPER_ADMIN. Retrieves complaint details by complaintId.\nWorkflow impact: inspect complaint data before vendor/manager actions.',
        response_examples(
            'Complaint Details',
            {'id': 100, 'status': 'ASSIGNED_TO_VENDOR', 'issueCategory': 'Battery Failure', 'priority': 'LOW', 'assignedTeam': 'VendorCo', 'data': '{"issueCategory":"Battery Failure","issueDescription":"Battery drains within 10 miles of charge.","location":"Warehouse district","severity":"HIGH"}', 'createdAt': '2026-05-09T12:00:00', 'customerId': '1', 'vehicleId': '300', 'vendorId': 200, 'latitude': 12.9716, 'longitude': 77.5946, 'escalationReason': None},
            200,
            {'status': 500, 'error': 'Internal Server Error', 'message': 'Complaint not found with ID: 9999'},
            500
        )
    ),
    request_item(
        'Submit User Workflow Response', 'POST', base_url + '/api/workflow/user-response',
        [{'key': 'Content-Type', 'value': 'application/json'}, {'key': 'Authorization', 'value': 'Bearer {{driverToken}}'}],
        {'taskId': '{{taskId}}', 'resolved': False, 'continueAi': True, 'userFollowUp': 'Please retry AI with deeper battery diagnostics.'},
        'RBAC: DRIVER only. Sends user decision into workflow.\nWorkflow impact: continues AI retry or resolves based on driver input.',
        response_examples(
            'User Workflow Response',
            'User response submitted successfully',
            200,
            {'status': 404, 'error': 'Internal Server Error', 'message': 'Task not found'},
            500
        )
    )
]))

collection['item'].append(folder('Complaints', [
    request_item(
        'Filter Complaints by Status', 'POST', base_url + '/api/complaints/filter/status',
        [{'key': 'Content-Type', 'value': 'application/json'}, {'key': 'Authorization', 'value': 'Bearer {{managerToken}}'}],
        {'status': 'ESCALATED_TO_MANAGER'},
        'RBAC: MANAGER + ADMIN + SUPER_ADMIN. Returns complaints matching the specified status.\nWorkflow impact: manager triage for escalated complaints.',
        response_examples(
            'Filter by Status',
            [{'id': 110, 'status': 'ESCALATED_TO_MANAGER', 'issueCategory': 'Charging Port Fault', 'priority': 'MEDIUM', 'assignedTeam': 'VendorCo', 'data': '{"issueCategory":"Charging Port Fault","issueDescription":"Connector overheating","location":"Depot","severity":"MEDIUM"}', 'createdAt': '2026-05-09T12:30:00', 'customerId': '2', 'vehicleId': '301', 'vendorId': 201, 'latitude': 12.9716, 'longitude': 77.5946, 'escalationReason': 'Vendor could not resolve issue'}],
            200,
            {'status': 403, 'error': 'Access Denied', 'message': 'You do not have permission to access this resource.'},
            403
        )
    ),
    request_item(
        'Filter Complaints by Vehicle', 'POST', base_url + '/api/complaints/filter/vehicle',
        [{'key': 'Content-Type', 'value': 'application/json'}, {'key': 'Authorization', 'value': 'Bearer {{vendorToken}}'}],
        {'vehicleId': '300'},
        'RBAC: VENDOR_ADMIN + MANAGER + ADMIN + SUPER_ADMIN. Returns complaints for a specific vehicle.\nWorkflow impact: correlate complaint history to a vehicle.',
        response_examples(
            'Filter by Vehicle',
            [{'id': 100, 'status': 'ASSIGNED_TO_VENDOR', 'issueCategory': 'Battery Failure', 'priority': 'LOW', 'assignedTeam': 'VendorCo', 'data': '{"issueCategory":"Battery Failure","issueDescription":"Battery drains within 10 miles of charge.","location":"Warehouse district","severity":"HIGH"}', 'createdAt': '2026-05-09T12:00:00', 'customerId': '1', 'vehicleId': '300', 'vendorId': 200, 'latitude': 12.9716, 'longitude': 77.5946, 'escalationReason': None}],
            200,
            {'status': 403, 'error': 'Access Denied', 'message': 'You do not have permission to access this resource.'},
            403
        )
    ),
    request_item(
        'Get Audit Logs by Complaint', 'POST', base_url + '/api/complaints/audit-logs',
        [{'key': 'Content-Type', 'value': 'application/json'}, {'key': 'Authorization', 'value': 'Bearer {{adminToken}}'}],
        {'complaintId': 100},
        'RBAC: ADMIN + SUPER_ADMIN. Retrieves audit log entries for a complaint.\nWorkflow impact: audit trail for complaint lifecycle changes.',
        response_examples(
            'Audit Logs',
            [{'id': 501, 'complaintId': 100, 'vehicleId': '300', 'action': 'CREATED', 'performedBy': 'USER', 'previousStatus': None, 'newStatus': 'OPEN', 'remarks': 'Complaint created by user', 'metadata': '{"userId":"1","issueCategory":"Battery Failure","issueDescription":"Battery drains within 10 miles of charge.","location":"Warehouse district","vehicleId":"300"}', 'createdAt': '2026-05-09T12:00:00'}],
            200,
            {'status': 403, 'error': 'Access Denied', 'message': 'You do not have permission to access this resource.'},
            403
        )
    ),
    request_item(
        'Get Assigned Complaints by Vendor Name', 'POST', base_url + '/api/complaints/assigned',
        [{'key': 'Content-Type', 'value': 'application/json'}, {'key': 'Authorization', 'value': 'Bearer {{vendorToken}}'}],
        {'vendorName': 'VendorCo'},
        'RBAC: VENDOR_ADMIN + MANAGER + ADMIN + SUPER_ADMIN. Vendor and managers can fetch complaints assigned to a vendor team.\nWorkflow impact: vendor routing and workload review.',
        response_examples(
            'Assigned Complaints',
            [{'id': 100, 'status': 'ASSIGNED_TO_VENDOR', 'issueCategory': 'Battery Failure', 'priority': 'LOW', 'assignedTeam': 'VendorCo', 'data': '{"issueCategory":"Battery Failure","issueDescription":"Battery drains within 10 miles of charge.","location":"Warehouse district","severity":"HIGH"}', 'createdAt': '2026-05-09T12:00:00', 'customerId': '1', 'vehicleId': '300', 'vendorId': 200, 'latitude': 12.9716, 'longitude': 77.5946, 'escalationReason': None}],
            200,
            {'status': 403, 'error': 'Access Denied', 'message': 'You do not have permission to access this resource.'},
            403
        )
    ),
    request_item(
        'Update Complaint Status', 'PUT', base_url + '/api/complaints/status',
        [{'key': 'Content-Type', 'value': 'application/json'}, {'key': 'Authorization', 'value': 'Bearer {{vendorToken}}'}],
        {'complaintId': 100, 'status': 'IN_REVIEW'},
        'RBAC: VENDOR_ADMIN + MANAGER. Updates complaint workflow status from vendor or manager perspective.\nWorkflow impact: tracks vendor progress and status changes.',
        response_examples(
            'Update Complaint Status',
            'Complaint status updated',
            200,
            {'status': 500, 'error': 'Internal Server Error', 'message': 'Complaint not found'},
            500
        )
    ),
    request_item(
        'Resolve Complaint', 'PUT', base_url + '/api/complaints/resolve',
        [{'key': 'Content-Type', 'value': 'application/json'}, {'key': 'Authorization', 'value': 'Bearer {{vendorToken}}'}],
        {'complaintId': 100, 'resolved': True, 'resolutionRemarks': 'Battery replacement completed successfully.'},
        'RBAC: VENDOR_ADMIN + MANAGER. Resolves or escalates the complaint based on vendor outcome.\nWorkflow impact: closes the workflow or escalates to manager.',
        response_examples(
            'Resolve Complaint',
            'Complaint resolved successfully',
            200,
            {'status': 500, 'error': 'Internal Server Error', 'message': 'Vendor task not found'},
            500
        )
    ),
    request_item(
        'Approve and Assign Complaint', 'PUT', base_url + '/api/complaints/assign',
        [{'key': 'Content-Type', 'value': 'application/json'}, {'key': 'Authorization', 'value': 'Bearer {{managerToken}}'}],
        {'complaintId': 100, 'teamName': 'VendorCo'},
        'RBAC: MANAGER + ADMIN + SUPER_ADMIN. Approves AI-created complaint and assigns it to a vendor team.\nWorkflow impact: transitions complaint into vendor assignment state.',
        response_examples(
            'Assign Complaint',
            {'id': 100, 'status': 'ASSIGNED_TO_VENDOR', 'issueCategory': 'Battery Failure', 'priority': 'LOW', 'assignedTeam': 'VendorCo', 'data': '{"issueCategory":"Battery Failure","issueDescription":"Battery drains within 10 miles of charge.","location":"Warehouse district","severity":"HIGH"}', 'createdAt': '2026-05-09T12:00:00', 'customerId': '1', 'vehicleId': '300', 'vendorId': 200, 'latitude': 12.9716, 'longitude': 77.5946, 'escalationReason': None},
            200,
            {'status': 500, 'error': 'Internal Server Error', 'message': 'Complaint not found'},
            500
        )
    ),
    request_item(
        'Reject Complaint', 'PUT', base_url + '/api/complaints/reject',
        [{'key': 'Content-Type', 'value': 'application/json'}, {'key': 'Authorization', 'value': 'Bearer {{managerToken}}'}],
        {'complaintId': 100},
        'RBAC: MANAGER + ADMIN + SUPER_ADMIN. Rejects a complaint from escalation or assignment workflows.\nWorkflow impact: sets complaint to REJECTED state.',
        response_examples(
            'Reject Complaint',
            {'id': 100, 'status': 'REJECTED', 'issueCategory': 'Battery Failure', 'priority': 'LOW', 'assignedTeam': 'VendorCo', 'data': '{"issueCategory":"Battery Failure","issueDescription":"Battery drains within 10 miles of charge.","location":"Warehouse district","severity":"HIGH"}', 'createdAt': '2026-05-09T12:00:00', 'customerId': '1', 'vehicleId': '300', 'vendorId': 200, 'latitude': 12.9716, 'longitude': 77.5946, 'escalationReason': None},
            200,
            {'status': 500, 'error': 'Internal Server Error', 'message': 'Complaint not found'},
            500
        )
    ),
    request_item(
        'Manager Decision', 'PUT', base_url + '/api/complaints/decision',
        [{'key': 'Content-Type', 'value': 'application/json'}, {'key': 'Authorization', 'value': 'Bearer {{managerToken}}'}],
        {'complaintId': 100, 'managerDecision': 'RETRY'},
        'RBAC: MANAGER + ADMIN + SUPER_ADMIN. Applies RETRY / RESOLVE / REJECT decisions at manager stage.\nWorkflow impact: directs the complaint to retry vendor work, resolve, or reject.',
        response_examples(
            'Manager Decision',
            'Manager decision updated',
            200,
            {'status': 500, 'error': 'Internal Server Error', 'message': 'Complaint not found'},
            500
        )
    ),
    request_item(
        'Get Available Vendors', 'GET', base_url + '/api/complaints/vendors',
        [{'key': 'Authorization', 'value': 'Bearer {{managerToken}}'}],
        None,
        'RBAC: MANAGER + ADMIN + SUPER_ADMIN. Lists approved and available vendors for assignment.\nWorkflow impact: vendor routing and assignment screening.',
        response_examples(
            'Available Vendors',
            [{'id': 200, 'approvalStatus': 'APPROVED', 'companyName': 'VendorCo', 'gstin': 'GSTIN1234', 'email': 'vendor@example.com', 'countryCode': '+91', 'phoneNumber': '+911112223334', 'addressLine1': 'Vendor Street 1', 'gstinDocumentUrl': 'https://example.com/gstin.pdf', 'panNumber': 'PAN1234', 'latitude': 12.9716, 'longitude': 77.5946, 'vendorRating': 4.5, 'vendorAvailability': True, 'expertise': 'Battery Repair'}],
            200,
            {'status': 403, 'error': 'Access Denied', 'message': 'You do not have permission to access this resource.'},
            403
        )
    ),
    request_item(
        'Reassign Vendor', 'PUT', base_url + '/api/complaints/reassign',
        [{'key': 'Content-Type', 'value': 'application/json'}, {'key': 'Authorization', 'value': 'Bearer {{managerToken}}'}],
        {'complaintId': 100, 'vendorId': 200},
        'RBAC: MANAGER + ADMIN + SUPER_ADMIN. Reassigns the complaint to a different vendor.\nWorkflow impact: handles vendor reassignment scenarios and same-vendor checks.',
        response_examples(
            'Reassign Vendor',
            {'id': 100, 'status': 'ASSIGNED_TO_VENDOR', 'issueCategory': 'Battery Failure', 'priority': 'LOW', 'assignedTeam': 'VendorCo', 'data': '{"issueCategory":"Battery Failure","issueDescription":"Battery drains within 10 miles of charge.","location":"Warehouse district","severity":"HIGH"}', 'createdAt': '2026-05-09T12:00:00', 'customerId': '1', 'vehicleId': '300', 'vendorId': 200, 'latitude': 12.9716, 'longitude': 77.5946, 'escalationReason': None},
            200,
            {'status': 500, 'error': 'Internal Server Error', 'message': 'Complaint not found'},
            500
        )
    )
]))

collection['item'].append(folder('Workflow', [
    request_item(
        'Vendor Workflow Response', 'POST', base_url + '/api/workflow/vendor-response',
        [{'key': 'Content-Type', 'value': 'application/json'}, {'key': 'Authorization', 'value': 'Bearer {{vendorToken}}'}],
        {'taskId': '{{taskId}}', 'vendorResolved': False},
        'RBAC: VENDOR_ADMIN only. Completes vendor workflow task with resolution status.\nWorkflow impact: progresses vendor task and may escalate to manager.',
        response_examples(
            'Vendor Workflow Response',
            'Vendor response submitted successfully',
            200,
            {'status': 404, 'error': 'Internal Server Error', 'message': 'Task not found'},
            500
        )
    ),
    request_item(
        'Manager Workflow Response', 'POST', base_url + '/api/workflow/manager-response',
        [{'key': 'Content-Type', 'value': 'application/json'}, {'key': 'Authorization', 'value': 'Bearer {{managerToken}}'}],
        {'taskId': '{{taskId}}', 'managerDecision': 'RESOLVE'},
        'RBAC: MANAGER + ADMIN + SUPER_ADMIN. Completes the manager task with a decision.\nWorkflow impact: closes escalated complaints or sends them back to retry.',
        response_examples(
            'Manager Workflow Response',
            'Manager response submitted successfully',
            200,
            {'status': 404, 'error': 'Internal Server Error', 'message': 'Task not found'},
            500
        )
    )
]))

collection['item'].append(folder('AI Retry', [
    request_item(
        'Save AI Query', 'POST', base_url + '/api/ai/queries',
        [{'key': 'Content-Type', 'value': 'application/json'}],
        {'userId': 1, 'vehicleId': '300', 'vehicleModel': 'EV-Model-X', 'question': 'Why is the battery range dropping?'},
        'RBAC: Public AI persistence endpoint. Saves AI query data for tracking and follow-up.\nWorkflow impact: captures AI retry query metadata for complaint resolution.',
        response_examples(
            'Save AI Query',
            {'id': 1, 'userId': 1, 'vehicleId': '300', 'vehicleModel': 'EV-Model-X', 'question': 'Why is the battery range dropping?', 'createdAt': '2026-05-09T12:00:00'},
            200,
            {'status': 400, 'error': 'Validation Failed', 'message': 'question is required'},
            400
        )
    ),
    request_item(
        'Save AI Response', 'POST', base_url + '/api/ai/responses',
        [{'key': 'Content-Type', 'value': 'application/json'}],
        {'queryId': 1, 'userId': 1, 'vehicleId': '300', 'issueId': 'ISSUE-123', 'answer': 'Battery cells show uneven discharge patterns.', 'confidence': 0.92, 'status': 'RECOMMENDED', 'title': 'Battery Health Analysis', 'description': 'AI recommends battery cell recalibration.'},
        'RBAC: Public AI persistence endpoint. Saves AI response details and status.\nWorkflow impact: records AI guidance for retry and resolution decisions.',
        response_examples(
            'Save AI Response',
            {'id': 1, 'queryId': 1, 'userId': 1, 'vehicleId': '300', 'issueId': 'ISSUE-123', 'answer': 'Battery cells show uneven discharge patterns.', 'confidence': 0.92, 'status': 'RECOMMENDED', 'title': 'Battery Health Analysis', 'description': 'AI recommends battery cell recalibration.', 'createdAt': '2026-05-09T12:10:00'},
            200,
            {'status': 500, 'error': 'Internal Server Error', 'message': 'Database write failed'},
            500
        )
    ),
    request_item(
        'Get Queries by User', 'GET', base_url + '/api/ai/queries/user/1',
        [{'key': 'Authorization', 'value': 'Bearer {{driverToken}}'}],
        None,
        'RBAC: Public GET endpoint. Lists AI queries for a user.\nWorkflow impact: supports AI retry auditing and complaint continuation.',
        response_examples(
            'AI Queries',
            [{'id': 1, 'userId': 1, 'vehicleId': '300', 'vehicleModel': 'EV-Model-X', 'question': 'Why is the battery range dropping?', 'createdAt': '2026-05-09T12:00:00'}],
            200,
            {'status': 401, 'error': 'Invalid Token', 'message': 'Token is malformed or tampered.'},
            401
        )
    ),
    request_item(
        'Get AI Responses by Query', 'GET', base_url + '/api/ai/responses/query/1',
        [{'key': 'Authorization', 'value': 'Bearer {{driverToken}}'}],
        None,
        'RBAC: Public GET endpoint. Lists AI responses for a query.\nWorkflow impact: verifies AI retry output during complaint resolution.',
        response_examples(
            'AI Responses',
            [{'id': 1, 'queryId': 1, 'userId': 1, 'vehicleId': '300', 'issueId': 'ISSUE-123', 'answer': 'Battery cells show uneven discharge patterns.', 'confidence': 0.92, 'status': 'RECOMMENDED', 'title': 'Battery Health Analysis', 'description': 'AI recommends battery cell recalibration.', 'createdAt': '2026-05-09T12:10:00'}],
            200,
            {'status': 401, 'error': 'Invalid Token', 'message': 'Token is malformed or tampered.'},
            401
        )
    )
]))

collection['item'].append(folder('Vendor', [
    request_item(
        'Get All Vendors', 'GET', base_url + '/api/vendors',
        [{'key': 'Authorization', 'value': 'Bearer {{managerToken}}'}],
        None,
        'RBAC: MANAGER + ADMIN + SUPER_ADMIN. Retrieves all vendor organizations.\nWorkflow impact: vendor directory review and selection.',
        response_examples(
            'All Vendors',
            [{'id': 200, 'approvalStatus': 'APPROVED', 'companyName': 'VendorCo', 'gstin': 'GSTIN1234', 'email': 'vendor@example.com', 'countryCode': '+91', 'phoneNumber': '+911112223334', 'addressLine1': 'Vendor Street 1', 'gstinDocumentUrl': 'https://example.com/gstin.pdf', 'panNumber': 'PAN1234', 'latitude': 12.9716, 'longitude': 77.5946, 'vendorRating': 4.5, 'vendorAvailability': True, 'expertise': 'Battery Repair'}],
            200,
            {'status': 403, 'error': 'Access Denied', 'message': 'You do not have permission to access this resource.'},
            403
        )
    ),
    request_item(
        'Get Vendor Details', 'POST', base_url + '/api/vendors/details',
        [{'key': 'Content-Type', 'value': 'application/json'}, {'key': 'Authorization', 'value': 'Bearer {{vendorToken}}'}],
        {'vendorId': 200},
        'RBAC: VENDOR_ADMIN + MANAGER + ADMIN + SUPER_ADMIN. Retrieves vendor organization details by vendorId.\nWorkflow impact: vendor profile inspection before assignment.',
        response_examples(
            'Vendor Details',
            {'id': 200, 'approvalStatus': 'APPROVED', 'companyName': 'VendorCo', 'gstin': 'GSTIN1234', 'email': 'vendor@example.com', 'countryCode': '+91', 'phoneNumber': '+911112223334', 'addressLine1': 'Vendor Street 1', 'gstinDocumentUrl': 'https://example.com/gstin.pdf', 'panNumber': 'PAN1234', 'latitude': 12.9716, 'longitude': 77.5946, 'vendorRating': 4.5, 'vendorAvailability': True, 'expertise': 'Battery Repair'},
            200,
            {'status': 500, 'error': 'Internal Server Error', 'message': 'Vendor not found with ID: 9999'},
            500
        )
    ),
    request_item(
        'Get Available Vendors', 'GET', base_url + '/api/vendors/available',
        [{'key': 'Authorization', 'value': 'Bearer {{managerToken}}'}],
        None,
        'RBAC: MANAGER + ADMIN + SUPER_ADMIN. Lists vendors currently marked available.\nWorkflow impact: availability filtering for vendor assignment.',
        response_examples(
            'Available Vendors',
            [{'id': 200, 'approvalStatus': 'APPROVED', 'companyName': 'VendorCo', 'gstin': 'GSTIN1234', 'email': 'vendor@example.com', 'countryCode': '+91', 'phoneNumber': '+911112223334', 'addressLine1': 'Vendor Street 1', 'gstinDocumentUrl': 'https://example.com/gstin.pdf', 'panNumber': 'PAN1234', 'latitude': 12.9716, 'longitude': 77.5946, 'vendorRating': 4.5, 'vendorAvailability': True, 'expertise': 'Battery Repair'}],
            200,
            {'status': 403, 'error': 'Access Denied', 'message': 'You do not have permission to access this resource.'},
            403
        )
    ),
    request_item(
        'Get Vendors by Expertise', 'POST', base_url + '/api/vendors/expertise',
        [{'key': 'Content-Type', 'value': 'application/json'}, {'key': 'Authorization', 'value': 'Bearer {{managerToken}}'}],
        {'expertise': 'Battery Repair'},
        'RBAC: MANAGER + ADMIN + SUPER_ADMIN. Filters vendors by expertise.\nWorkflow impact: matches vendors to complaint issue category.',
        response_examples(
            'Vendors by Expertise',
            [{'id': 200, 'approvalStatus': 'APPROVED', 'companyName': 'VendorCo', 'gstin': 'GSTIN1234', 'email': 'vendor@example.com', 'countryCode': '+91', 'phoneNumber': '+911112223334', 'addressLine1': 'Vendor Street 1', 'gstinDocumentUrl': 'https://example.com/gstin.pdf', 'panNumber': 'PAN1234', 'latitude': 12.9716, 'longitude': 77.5946, 'vendorRating': 4.5, 'vendorAvailability': True, 'expertise': 'Battery Repair'}],
            200,
            {'status': 403, 'error': 'Access Denied', 'message': 'You do not have permission to access this resource.'},
            403
        )
    ),
    request_item(
        'Get Vendors by Availability', 'POST', base_url + '/api/vendors/availability',
        [{'key': 'Content-Type', 'value': 'application/json'}, {'key': 'Authorization', 'value': 'Bearer {{managerToken}}'}],
        {'availability': True},
        'RBAC: MANAGER + ADMIN + SUPER_ADMIN. Filters vendors by current availability status.\nWorkflow impact: excludes unavailable vendors from assignment.',
        response_examples(
            'Vendors by Availability',
            [{'id': 200, 'approvalStatus': 'APPROVED', 'companyName': 'VendorCo', 'gstin': 'GSTIN1234', 'email': 'vendor@example.com', 'countryCode': '+91', 'phoneNumber': '+911112223334', 'addressLine1': 'Vendor Street 1', 'gstinDocumentUrl': 'https://example.com/gstin.pdf', 'panNumber': 'PAN1234', 'latitude': 12.9716, 'longitude': 77.5946, 'vendorRating': 4.5, 'vendorAvailability': True, 'expertise': 'Battery Repair'}],
            200,
            {'status': 403, 'error': 'Access Denied', 'message': 'You do not have permission to access this resource.'},
            403
        )
    )
]))

collection['item'].append(folder('Manager', [
    request_item(
        'Manager complaint list', 'GET', base_url + '/api/complaints',
        [{'key': 'Authorization', 'value': 'Bearer {{managerToken}}'}],
        None,
        'RBAC: MANAGER + ADMIN + SUPER_ADMIN. Manager view of escalated complaints.\nWorkflow impact: manager dashboard and escalation queue.',
        response_examples(
            'Manager Complaints',
            [{'id': 110, 'status': 'ESCALATED_TO_MANAGER', 'issueCategory': 'Charging Port Fault', 'priority': 'MEDIUM', 'assignedTeam': 'VendorCo', 'data': '{"issueCategory":"Charging Port Fault","issueDescription":"Connector overheating","location":"Depot","severity":"MEDIUM"}', 'createdAt': '2026-05-09T12:30:00', 'customerId': '2', 'vehicleId': '301', 'vendorId': 201, 'latitude': 12.9716, 'longitude': 77.5946, 'escalationReason': 'Vendor could not resolve issue'}],
            200,
            {'status': 403, 'error': 'Access Denied', 'message': 'You do not have permission to access this resource.'},
            403
        )
    )
]))

collection['item'].append(folder('Audit Logs', [
    request_item(
        'Get Audit Logs by Action', 'POST', base_url + '/api/audit-logs/action',
        [{'key': 'Content-Type', 'value': 'application/json'}, {'key': 'Authorization', 'value': 'Bearer {{adminToken}}'}],
        {'action': 'VENDOR_ASSIGNED'},
        'RBAC: ADMIN + SUPER_ADMIN. Lists audit entries by action type.\nWorkflow impact: validate workflow transitions and vendor operations.',
        response_examples(
            'Audit Logs by Action',
            [{'id': 502, 'complaintId': 100, 'vehicleId': '300', 'action': 'VENDOR_ASSIGNED', 'performedBy': 'SYSTEM', 'previousStatus': 'AI_SUPPORT', 'newStatus': 'ASSIGNED_TO_VENDOR', 'remarks': 'Complaint assigned to nearest available vendor', 'metadata': '{"vendorName":"VendorCo","distanceKm": 12.3}', 'createdAt': '2026-05-09T12:05:00'}],
            200,
            {'status': 403, 'error': 'Access Denied', 'message': 'You do not have permission to access this resource.'},
            403
        )
    ),
    request_item(
        'Get Audit Logs by Vehicle', 'POST', base_url + '/api/audit-logs/vehicle',
        [{'key': 'Content-Type', 'value': 'application/json'}, {'key': 'Authorization', 'value': 'Bearer {{adminToken}}'}],
        {'vehicleId': '300'},
        'RBAC: ADMIN + SUPER_ADMIN. Lists audit logs for a vehicle.\nWorkflow impact: vehicle-related history and complaint traceability.',
        response_examples(
            'Audit Logs by Vehicle',
            [{'id': 501, 'complaintId': 100, 'vehicleId': '300', 'action': 'CREATED', 'performedBy': 'USER', 'previousStatus': None, 'newStatus': 'OPEN', 'remarks': 'Complaint created by user', 'metadata': '{"issueCategory":"Battery Failure"}', 'createdAt': '2026-05-09T12:00:00'}],
            200,
            {'status': 403, 'error': 'Access Denied', 'message': 'You do not have permission to access this resource.'},
            403
        )
    )
]))

collection['item'].append(folder('Vehicles', [
    request_item(
        'Add Vehicle', 'POST', base_url + '/api/vehicles',
        [{'key': 'Content-Type', 'value': 'application/json'}, {'key': 'Authorization', 'value': 'Bearer {{adminToken}}'}],
        {'userId': 1, 'make': 'EV', 'model': 'Model X', 'licensePlate': 'EV-1234', 'vin': 'VIN123456789', 'status': 'ACTIVE', 'yearOfManufacture': 2025, 'batteryCapacityKwh': 95.0, 'chassisNo': 'CHASSIS-001'},
        'RBAC: ADMIN only. Adds a new vehicle record.\nWorkflow impact: vehicle metadata for complaint routing and service history.',
        response_examples(
            'Add Vehicle',
            {'message': 'Vehicle added successfully', 'data': {'id': 300, 'userId': 1, 'make': 'EV', 'model': 'Model X', 'licensePlate': 'EV-1234', 'vin': 'VIN123456789', 'status': 'ACTIVE', 'yearOfManufacture': 2025, 'batteryCapacityKwh': 95.0, 'chassisNo': 'CHASSIS-001'}},
            200,
            {'status': 500, 'error': 'Internal Server Error', 'message': 'Database write failed'},
            500
        )
    ),
    request_item(
        'Get Vehicle Details', 'GET', base_url + '/api/vehicles/300',
        [{'key': 'Authorization', 'value': 'Bearer {{adminToken}}'}],
        None,
        'RBAC: ADMIN only. Retrieves a vehicle by ID.\nWorkflow impact: verify vehicle assignment before complaint creation.',
        response_examples(
            'Get Vehicle',
            {'message': 'Vehicle fetched successfully', 'data': {'id': 300, 'userId': 1, 'make': 'EV', 'model': 'Model X', 'licensePlate': 'EV-1234', 'vin': 'VIN123456789', 'status': 'ACTIVE', 'yearOfManufacture': 2025, 'batteryCapacityKwh': 95.0, 'chassisNo': 'CHASSIS-001'}},
            200,
            {'status': 404, 'error': 'Vehicle Not Found', 'message': 'Vehicle not found with id 999'},
            404
        )
    ),
    request_item(
        'Add Service History Entry', 'POST', base_url + '/api/service-history',
        [{'key': 'Content-Type', 'value': 'application/json'}, {'key': 'Authorization', 'value': 'Bearer {{adminToken}}'}],
        {'vehicleId': 300, 'serviceDate': '2026-05-01', 'odometerReading': 12000, 'serviceType': 'BATTERY_SERVICE', 'description': 'Battery health inspection and recalibration', 'totalCost': 1200.50, 'providerName': 'EV Service Center'},
        'RBAC: ADMIN only. Adds vehicle service history.\nWorkflow impact: supports vehicle maintenance tracking for complaint correlation.',
        response_examples(
            'Add Service History',
            {'message': 'Service entry added successfully', 'data': {'id': 500, 'vehicleId': 300, 'serviceDate': '2026-05-01', 'odometerReading': 12000, 'serviceType': 'BATTERY_SERVICE', 'description': 'Battery health inspection and recalibration', 'totalCost': 1200.50, 'providerName': 'EV Service Center', 'createdAt': '2026-05-01T10:00:00', 'updatedAt': '2026-05-01T10:00:00'}},
            200,
            {'status': 500, 'error': 'Internal Server Error', 'message': 'Save failed'},
            500
        )
    ),
    request_item(
        'Get Recent Service History', 'GET', base_url + '/api/service-history/vehicle/300',
        [{'key': 'Authorization', 'value': 'Bearer {{adminToken}}'}],
        None,
        'RBAC: ADMIN only. Retrieves service history for a vehicle.\nWorkflow impact: validates maintenance state for complaint triage.',
        response_examples(
            'Service History',
            {'message': 'Service history fetched successfully', 'data': [{'id': 500, 'vehicleId': 300, 'serviceDate': '2026-05-01', 'odometerReading': 12000, 'serviceType': 'BATTERY_SERVICE', 'description': 'Battery health inspection and recalibration', 'totalCost': 1200.50, 'providerName': 'EV Service Center', 'createdAt': '2026-05-01T10:00:00', 'updatedAt': '2026-05-01T10:00:00'}]},
            200,
            {'status': 404, 'error': 'Service History Not Found', 'message': 'No service history found for vehicle 999'},
            404
        )
    )
]))

collection['item'].append(folder('Admin', [
    request_item(
        'Get Users', 'GET', base_url + '/api/users',
        [{'key': 'Authorization', 'value': 'Bearer {{adminToken}}'}],
        None,
        'RBAC: SUPER_ADMIN + ADMIN + VENDOR_ADMIN. Returns users filtered by caller role and optional approval status.\nWorkflow impact: admin user management and vendor/driver oversight.',
        response_examples(
            'Get Users',
            {'message': 'Users fetched successfully', 'data': [{'id': 1, 'email': 'driver@example.com', 'role': 'DRIVER', 'userType': 'INDIVIDUAL', 'approvalStatus': 'APPROVED', 'phoneNumber': '+911234567890', 'companyName': None, 'fullName': 'Driver One', 'panNumber': None, 'gstin': None, 'gstinDocumentUrl': None, 'companyApprovalStatus': None, 'vendorRating': None, 'vendorAvailability': None, 'expertise': None}]},
            200,
            {'status': 403, 'error': 'Access Denied', 'message': 'You do not have permission to access this resource.'},
            403
        )
    ),
    request_item(
        'Get Organizations', 'GET', base_url + '/api/users/organizations',
        [{'key': 'Authorization', 'value': 'Bearer {{adminToken}}'}],
        None,
        'RBAC: SUPER_ADMIN + ADMIN. Retrieves organization accounts by approval status.\nWorkflow impact: vendor and company onboarding review.',
        response_examples(
            'Get Organizations',
            {'message': 'Organizations fetched successfully', 'data': [{'id': 200, 'companyName': 'VendorCo', 'email': 'vendor@example.com', 'phoneNumber': '+911112223334', 'gstin': 'GSTIN1234', 'panNumber': 'PAN1234', 'approvalStatus': 'APPROVED', 'latitude': 12.9716, 'longitude': 77.5946, 'gstinDocumentUrl': 'https://example.com/gstin.pdf', 'vendorRating': 4.5, 'vendorAvailability': True, 'expertise': 'Battery Repair'}]},
            200,
            {'status': 403, 'error': 'Access Denied', 'message': 'You do not have permission to access this resource.'},
            403
        )
    ),
    request_item(
        'Get Individuals', 'GET', base_url + '/api/users/individuals',
        [{'key': 'Authorization', 'value': 'Bearer {{adminToken}}'}],
        None,
        'RBAC: SUPER_ADMIN + ADMIN + VENDOR_ADMIN. Retrieves individual user profiles.\nWorkflow impact: individual driver and technician onboarding management.',
        response_examples(
            'Get Individuals',
            {'message': 'Individuals fetched successfully', 'data': [{'id': 1, 'email': 'driver@example.com', 'role': 'DRIVER', 'userType': 'INDIVIDUAL', 'approvalStatus': 'APPROVED', 'phoneNumber': '+911234567890', 'companyName': None, 'fullName': 'Driver One', 'panNumber': None, 'gstin': None, 'gstinDocumentUrl': None, 'companyApprovalStatus': None, 'vendorRating': None, 'vendorAvailability': None, 'expertise': None}]},
            200,
            {'status': 403, 'error': 'Access Denied', 'message': 'You do not have permission to access this resource.'},
            403
        )
    ),
    request_item(
        'Assign Driver to Vehicle', 'PUT', base_url + '/api/users/assign-vehicle',
        [{'key': 'Content-Type', 'value': 'application/json'}, {'key': 'Authorization', 'value': 'Bearer {{adminToken}}'}],
        {'vehicleId': 300, 'driverId': 1},
        'RBAC: SUPER_ADMIN + ADMIN + VENDOR_ADMIN. Links a driver to a vehicle.\nWorkflow impact: enables drivers to create complaints for assigned vehicles.',
        response_examples(
            'Assign Driver',
            {'message': 'Driver assigned to vehicle successfully', 'data': None},
            200,
            {'status': 500, 'error': 'Internal Server Error', 'message': 'Assignment failed'},
            500
        )
    ),
    request_item(
        'Update Vendor Rating', 'PUT', base_url + '/api/users/{{targetUserId}}/rating',
        [{'key': 'Content-Type', 'value': 'application/json'}, {'key': 'Authorization', 'value': 'Bearer {{adminToken}}'}],
        {'rating': 4.8},
        'RBAC: SUPER_ADMIN + ADMIN. Updates vendor rating after complaint completion.\nWorkflow impact: vendor performance tracking.',
        response_examples(
            'Update Rating',
            {'message': 'Vendor rating updated successfully', 'data': None},
            200,
            {'status': 403, 'error': 'Access Denied', 'message': 'You do not have permission to access this resource.'},
            403
        )
    ),
    request_item(
        'Update Approval Status', 'POST', base_url + '/api/status/update',
        [{'key': 'Content-Type', 'value': 'application/json'}, {'key': 'Authorization', 'value': 'Bearer {{adminToken}}'}],
        {'targetId': 200, 'status': 'APPROVED'},
        'RBAC: SUPER_ADMIN + ADMIN + VENDOR_ADMIN. Updates approval status for users or vendors.\nWorkflow impact: enables onboarding status transition for vendors and individuals.',
        response_examples(
            'Update Status',
            {'message': 'Status updated successfully', 'data': 'Success'},
            200,
            {'status': 403, 'error': 'Access Denied', 'message': 'You do not have permission to access this resource.'},
            403
        )
    )
]))

collection['item'].append(folder('Edge Cases', [
    request_item(
        'Invalid JWT Access', 'GET', base_url + '/api/complaints',
        [{'key': 'Authorization', 'value': '{{invalidJwt}}'}],
        None,
        'Edge case: invalid JWT token. Confirms that malformed or invalid tokens return HTTP 401.\nRBAC impact: unauthorized authentication failure.',
        response_examples(
            'Invalid JWT',
            {},
            401,
            {'status': 401, 'error': 'Invalid Token', 'message': 'Token is malformed or tampered.'},
            401
        )
    ),
    request_item(
        'Unauthorized Role Access', 'POST', base_url + '/api/vehicles',
        [{'key': 'Content-Type', 'value': 'application/json'}, {'key': 'Authorization', 'value': 'Bearer {{driverToken}}'}],
        {'userId': 1, 'make': 'EV', 'model': 'Model Z', 'licensePlate': 'EV-4321', 'vin': 'VIN987654321', 'status': 'ACTIVE', 'yearOfManufacture': 2025, 'batteryCapacityKwh': 85.0, 'chassisNo': 'CHASSIS-002'},
        'Edge case: driver cannot access ADMIN-only vehicle endpoints.\nRBAC impact: verifies role enforcement.',
        response_examples(
            'Unauthorized Role',
            {},
            403,
            {'status': 403, 'error': 'Access Denied', 'message': 'You do not have permission to access this resource.'},
            403
        )
    ),
    request_item(
        'Create Complaint Missing Data', 'POST', base_url + '/api/complaints',
        [{'key': 'Content-Type', 'value': 'application/json'}, {'key': 'Authorization', 'value': 'Bearer {{driverToken}}'}],
        {'complaintData': None, 'latitude': 12.9716, 'longitude': 77.5946},
        'Edge case: complaint submission without complaintData. Backend returns an error string from service and avoids workflow start.',
        response_examples(
            'Missing Complaint Data',
            {},
            500,
            {'status': 500, 'error': 'Internal Server Error', 'message': 'Complaint data is missing'},
            500
        )
    ),
    request_item(
        'Create Complaint Missing Location', 'POST', base_url + '/api/complaints',
        [{'key': 'Content-Type', 'value': 'application/json'}, {'key': 'Authorization', 'value': 'Bearer {{driverToken}}'}],
        {'complaintData': {'issueCategory': 'Battery Failure', 'issueDescription': 'Battery drains early.'}},
        'Edge case: complaint without latitude/longitude. Workflow vendor assignment falls back to mock coordinates if location is missing.',
        response_examples(
            'Missing Location',
            {'message': 'Complaint saved & workflow started', 'payloadSentToAI': {'complaintData': {'issueCategory': 'Battery Failure', 'issueDescription': 'Battery drains early.'}, 'latitude': None, 'longitude': None}},
            200,
            {'status': 500, 'error': 'Internal Server Error', 'message': 'Missing location coordinates'},
            500
        )
    ),
    request_item(
        'No Vendors Available', 'GET', base_url + '/api/complaints/vendors',
        [{'key': 'Authorization', 'value': 'Bearer {{managerToken}}'}],
        None,
        'Edge case: no approved available vendors. If none exist the service returns an empty array.\nWorkflow impact: checks vendor availability filtering behavior.',
        response_examples(
            'No Vendors Available',
            [],
            200,
            {'status': 200, 'error': 'No vendors found', 'message': 'Empty vendor list'},
            200
        )
    ),
    request_item(
        'Duplicate Complaint Submission', 'POST', base_url + '/api/complaints',
        [{'key': 'Content-Type', 'value': 'application/json'}, {'key': 'Authorization', 'value': 'Bearer {{driverToken}}'}],
        {'complaintData': {'issueCategory': 'Battery Failure', 'issueDescription': 'Battery drains within 10 miles of charge.', 'location': 'Warehouse district'}, 'latitude': 12.9716, 'longitude': 77.5946},
        'Edge case: duplicate complaint payload. The implemented service does not reject duplicates, so repeated submissions create separate complaint records.',
        response_examples(
            'Duplicate Complaint',
            {'message': 'Complaint saved & workflow started', 'payloadSentToAI': {'complaintData': {'issueCategory': 'Battery Failure', 'issueDescription': 'Battery drains within 10 miles of charge.', 'location': 'Warehouse district'}, 'latitude': 12.9716, 'longitude': 77.5946}},
            200,
            {'status': 500, 'error': 'Internal Server Error', 'message': 'Duplicate complaint not allowed'},
            500
        )
    ),
    request_item(
        'Invalid Manager Decision', 'PUT', base_url + '/api/complaints/decision',
        [{'key': 'Content-Type', 'value': 'application/json'}, {'key': 'Authorization', 'value': 'Bearer {{managerToken}}'}],
        {'complaintId': 100, 'managerDecision': 'INVALID_DECISION'},
        'Edge case: invalid manager decision string. The controller accepts the value and returns the update result.\nRBAC impact: verify behavior for unexpected decision values.',
        response_examples(
            'Invalid Manager Decision',
            'Manager decision updated',
            200,
            {'status': 400, 'error': 'Bad Request', 'message': 'Invalid decision value'},
            400
        )
    ),
    request_item(
        'Invalid Complaint ID', 'POST', base_url + '/api/complaints/details',
        [{'key': 'Content-Type', 'value': 'application/json'}, {'key': 'Authorization', 'value': 'Bearer {{managerToken}}'}],
        {'complaintId': 9999},
        'Edge case: invalid complaintId for details lookup. Global exception handler returns not found or internal server error depending on implementation.',
        response_examples(
            'Invalid Complaint ID',
            {},
            500,
            {'status': 500, 'error': 'Internal Server Error', 'message': 'Complaint not found with ID: 9999'},
            500
        )
    )
]))

collection['item'].append(folder('Manager Workflow', [
    request_item(
        'Get Nearby Vendors for Complaint', 'POST', base_url + '/api/complaints/{{complaintId}}/nearby-vendors',
        [{'key': 'Authorization', 'value': 'Bearer {{managerToken}}'}],
        None,
        'RBAC: MANAGER + ADMIN + SUPER_ADMIN. Lists vendor recommendations by distance and expertise.\nWorkflow impact: supports vendor reassignment and routing.',
        response_examples(
            'Nearby Vendors',
            [{'vendorId': 200, 'companyName': 'VendorCo', 'email': 'vendor@example.com', 'phoneNumber': '+911112223334', 'address': 'Vendor Street 1', 'rating': 4.5, 'availability': True, 'expertise': 'Battery Repair', 'distanceKm': 12.3}],
            200,
            {'status': 500, 'error': 'Internal Server Error', 'message': 'Complaint not found'},
            500
        )
    )
]))

with open('FINAL_POSTMAN_COLLECTION.json', 'w', encoding='utf-8') as f:
    json.dump(collection, f, indent=2)

env = {
    'id': str(uuid.uuid4()),
    'name': 'EV Fleet Mobility Environment',
    'values': [
        {'key': 'baseUrl', 'value': 'http://localhost:8080', 'enabled': True},
        {'key': 'driverToken', 'value': '', 'enabled': True},
        {'key': 'vendorToken', 'value': '', 'enabled': True},
        {'key': 'managerToken', 'value': '', 'enabled': True},
        {'key': 'adminToken', 'value': '', 'enabled': True},
        {'key': 'superAdminToken', 'value': '', 'enabled': True},
        {'key': 'refreshToken', 'value': '', 'enabled': True},
        {'key': 'invalidJwt', 'value': 'Bearer invalid.token example', 'enabled': True},
        {'key': 'complaintId', 'value': '100', 'enabled': True},
        {'key': 'taskId', 'value': 'task-123', 'enabled': True},
        {'key': 'queryId', 'value': '1', 'enabled': True},
        {'key': 'vendorId', 'value': '200', 'enabled': True},
        {'key': 'vehicleId', 'value': '300', 'enabled': True},
        {'key': 'targetUserId', 'value': '400', 'enabled': True},
        {'key': 'driverEmail', 'value': 'driver@example.com', 'enabled': True},
        {'key': 'driverPassword', 'value': 'DriverPass1!', 'enabled': True},
        {'key': 'adminEmail', 'value': 'admin@example.com', 'enabled': True},
        {'key': 'adminPassword', 'value': 'AdminPass1!', 'enabled': True}
    ],
    'timestamp': 0,
    '_postman_variable_scope': 'environment',
    '_postman_exported_at': '2026-05-09T00:00:00.000Z',
    '_postman_exported_using': 'Postman/10.0'
}
with open('POSTMAN_ENVIRONMENT.json', 'w', encoding='utf-8') as f:
    json.dump(env, f, indent=2)

workflow = '''# EV Fleet Mobility Complaint Resolution System Workflow Test Sequence

1. Auth: Login as Driver
   - Request: POST {{baseUrl}}/api/auth/login
   - Body: {"email": "driver@example.com", "password": "DriverPass1!"}
   - Store {{driverToken}} and {{refreshToken}}

2. Driver: Create Complaint
   - Request: POST {{baseUrl}}/api/complaints
   - Headers: Authorization: Bearer {{driverToken}}
   - Body: complaintData with issueCategory, issueDescription, location and latitude/longitude
   - Workflow impact: starts complaint workflow and AI assessment

3. AI Retry: Save AI Query
   - Request: POST {{baseUrl}}/api/ai/queries
   - Body: userId, vehicleId, vehicleModel, question
   - Workflow impact: logs AI retry query for complaint follow-up

4. AI Retry: Save AI Response
   - Request: POST {{baseUrl}}/api/ai/responses
   - Body: queryId, userId, vehicleId, issueId, answer, confidence, status, title, description
   - Workflow impact: records AI response used in complaint decision-making

5. Workflow: Continue AI (continueAi = true)
   - Request: POST {{baseUrl}}/api/workflow/user-response
   - Body: {"taskId": "{{taskId}}", "resolved": false, "continueAi": true, "userFollowUp": "Retry AI with deeper diagnostics."}
   - RBAC: DRIVER

6. Workflow: Stop AI (continueAi = false)
   - Request: POST {{baseUrl}}/api/workflow/user-response
   - Body: {"taskId": "{{taskId}}", "resolved": false, "continueAi": false, "userFollowUp": "Proceed without AI."}
   - RBAC: DRIVER

7. Vendor: Get Available Vendors
   - Request: GET {{baseUrl}}/api/complaints/vendors
   - RBAC: MANAGER

8. Vendor: Get Nearby Vendors for Complaint
   - Request: POST {{baseUrl}}/api/complaints/{{complaintId}}/nearby-vendors
   - RBAC: MANAGER

9. Manager: Approve and Assign Vendor
   - Request: PUT {{baseUrl}}/api/complaints/assign
   - Body: {"complaintId": {{complaintId}}, "teamName": "VendorCo"}
   - RBAC: MANAGER

10. Vendor: Resolve Complaint
    - Request: PUT {{baseUrl}}/api/complaints/resolve
    - Body: {"complaintId": {{complaintId}}, "resolved": true, "resolutionRemarks": "Battery replacement completed successfully."}
    - RBAC: VENDOR_ADMIN

11. Vendor: Escalate Complaint
    - Request: PUT {{baseUrl}}/api/complaints/resolve
    - Body: {"complaintId": {{complaintId}}, "resolved": false, "resolutionRemarks": "Issue not fixable, escalate to manager."}
    - RBAC: VENDOR_ADMIN

12. Manager: Dashboard View
    - Request: GET {{baseUrl}}/api/complaints
    - RBAC: MANAGER
    - Expect escalated complaints status ESCALATED_TO_MANAGER

13. Manager: RETRY Decision
    - Request: PUT {{baseUrl}}/api/complaints/decision
    - Body: {"complaintId": {{complaintId}}, "managerDecision": "RETRY"}
    - RBAC: MANAGER

14. Manager: RESOLVE Decision
    - Request: PUT {{baseUrl}}/api/complaints/decision
    - Body: {"complaintId": {{complaintId}}, "managerDecision": "RESOLVE"}
    - RBAC: MANAGER

15. Manager: REJECT Decision
    - Request: PUT {{baseUrl}}/api/complaints/decision
    - Body: {"complaintId": {{complaintId}}, "managerDecision": "REJECT"}
    - RBAC: MANAGER

16. Audit: Fetch Complaint Audit Logs
    - Request: POST {{baseUrl}}/api/complaints/audit-logs
    - Body: {"complaintId": {{complaintId}}}
    - RBAC: ADMIN

17. Complaint Tracking: Retrieve Complaint Details
    - Request: POST {{baseUrl}}/api/complaints/details
    - Body: {"complaintId": {{complaintId}}}
    - RBAC: any authorized role

18. Edge Case: Invalid JWT
    - Request: GET {{baseUrl}}/api/complaints
    - Header: Authorization: {{invalidJwt}}

19. Edge Case: Unauthorized Role
    - Request: POST {{baseUrl}}/api/vehicles
    - Header: Authorization: Bearer {{driverToken}}

20. Edge Case: Missing Complaint Data
    - Request: POST {{baseUrl}}/api/complaints
    - Body: {"complaintData": null, "latitude": 12.9716, "longitude": 77.5946}

21. Edge Case: Missing Location
    - Request: POST {{baseUrl}}/api/complaints
    - Body: {"complaintData": {"issueCategory": "Battery Failure", "issueDescription": "Battery drains early."}}

22. Edge Case: No Vendors Available
    - Request: GET {{baseUrl}}/api/complaints/vendors

23. Edge Case: Same Vendor Reassignment
    - Request: PUT {{baseUrl}}/api/complaints/reassign
    - Body: {"complaintId": {{complaintId}}, "vendorId": {{vendorId}}}

24. Edge Case: Duplicate Complaints
    - Repeat Create Complaint with same complaintData.

25. Edge Case: Invalid Manager Decision
    - Request: PUT {{baseUrl}}/api/complaints/decision
    - Body: {"complaintId": {{complaintId}}, "managerDecision": "INVALID_DECISION"}

26. Edge Case: Invalid Complaint ID
    - Request: POST {{baseUrl}}/api/complaints/details
    - Body: {"complaintId": 9999}
'''
with open('WORKFLOW_TEST_SEQUENCE.md', 'w', encoding='utf-8') as f:
    f.write(workflow)
print('Wrote files successfully')
