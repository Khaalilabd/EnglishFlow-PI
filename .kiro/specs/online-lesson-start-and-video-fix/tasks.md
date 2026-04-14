# Implementation Plan: Online Lesson Start and Video Fix

## Overview

This implementation adds early lesson start capability (15 minutes before scheduled time) and fixes critical WebRTC bidirectional video streaming issues. The work involves frontend UI changes for time-aware button logic, WebRTC stream handling improvements in both tutor and student components, and signaling server enhancements for proper media renegotiation.

## Tasks

- [x] 1. Implement time-based lesson start button logic in LessonManagementComponent
  - [x] 1.1 Add canStartLesson() method to check if current time is within early start window
    - Implement logic to check if current time is between (scheduledStartTime - 15 minutes) and scheduledEndTime
    - Verify current day matches lesson's assigned day of week
    - Return boolean indicating button availability
    - _Requirements: 1.1, 1.2, 2.1_
  
  - [x] 1.2 Add getTimeUntilEarlyStart() method to calculate countdown display
    - Calculate time difference between current time and early start window
    - Format output as human-readable string (e.g., "2 hours 15 minutes")
    - Handle edge cases like negative values and midnight crossings
    - _Requirements: 1.4_
  
  - [x] 1.3 Update lesson preview modal template to show Start Lesson button
    - Add button with [disabled] binding to canStartLesson() result
    - Display countdown text using getTimeUntilEarlyStart() when button disabled
    - Add click handler to navigate to instant-meeting route with lesson parameters
    - Show notification when lesson time has ended
    - _Requirements: 1.3, 2.3_
  
  - [ ]* 1.4 Write property test for early start window button availability
    - **Property 1: Early Start Window Button Availability**
    - **Validates: Requirements 1.1, 2.1**
    - Generate random lesson schedules and current times
    - Verify button enabled only within 15-minute early start window until end time
    - Test across different days of week
  
  - [ ]* 1.5 Write unit tests for time calculation edge cases
    - Test exactly at 15 minutes before start time
    - Test at 16 minutes before (should be disabled)
    - Test at scheduled end time boundary
    - Test midnight crossing scenarios
    - _Requirements: 1.1, 1.2, 1.4, 2.1, 2.2_

- [x] 2. Fix WebRTC stream identification in InstantMeetingComponent (tutor side)
  - [x] 2.1 Update ontrack handler to identify stream types by track composition
    - Check stream for video and audio tracks
    - If video-only: assign to participant.screenStream and set isScreenSharing=true
    - If video+audio: assign to participant.cameraStream and set isCameraOff=false
    - If audio-only: attach to existing cameraStream or create new
    - Remove reliance on isScreenSharing flag timing
    - _Requirements: 3.1, 4.1, 5.1_
  
  - [x] 2.2 Implement onnegotiationneeded handler for media renegotiation
    - Create SDP offer when negotiation needed
    - Set local description with offer
    - Emit offer to signaling server with isRenegotiation flag
    - Add error handling for renegotiation failures
    - _Requirements: 7.4, 7.5_
  
  - [x] 2.3 Update participant interface to separate cameraStream and screenStream
    - Modify Participant interface to have distinct MediaStream properties
    - Update template bindings to use correct stream for video elements
    - Ensure both streams can be displayed simultaneously
    - _Requirements: 3.1, 4.1, 5.3_
  
  - [ ]* 2.4 Write property test for bidirectional media stream transmission
    - **Property 4: Bidirectional Media Stream Transmission**
    - **Validates: Requirements 3.1, 4.1, 5.1**
    - Generate random participant configurations with various media states
    - Verify all participants receive streams from all other participants
    - Test with camera-only, screen-only, and both stream types
  
  - [ ]* 2.5 Write unit tests for stream type identification
    - Test ontrack with video+audio stream (should identify as camera)
    - Test ontrack with video-only stream (should identify as screen share)
    - Test ontrack with audio-only stream (should attach to camera)
    - Test multiple tracks arriving in sequence
    - _Requirements: 3.1, 4.1_

- [x] 3. Fix WebRTC stream identification in MeetingJoinComponent (student side)
  - [x] 3.1 Update ontrack handler with same stream identification logic
    - Implement identical track composition checking as tutor component
    - Distinguish camera streams from screen shares
    - Handle audio-only tracks appropriately
    - _Requirements: 3.1, 3.2, 4.1, 4.2_
  
  - [x] 3.2 Implement onnegotiationneeded handler for student peer connections
    - Mirror renegotiation logic from tutor component
    - Handle incoming renegotiation offers from tutor
    - Ensure proper SDP answer generation
    - _Requirements: 7.1, 7.2, 7.4_
  
  - [x] 3.3 Add error handling and connection status display
    - Catch and log media stream transmission failures
    - Display connection status messages to students
    - Show placeholder for failed camera feeds
    - Implement automatic reconnection on connection failure
    - _Requirements: 3.3, 4.5, 5.5, 6.5_
  
  - [ ]* 3.4 Write property test for stream removal synchronization
    - **Property 6: Stream Removal Synchronization**
    - **Validates: Requirements 4.4, 5.4**
    - Generate random stream start/stop sequences
    - Verify UI updates within 2 seconds of stream removal
    - Test with multiple simultaneous stream changes

- [x] 4. Checkpoint - Verify frontend WebRTC changes
  - Ensure all tests pass, ask the user if questions arise.

- [x] 5. Enhance WebRTC signaling server for proper media renegotiation
  - [x] 5.1 Update offer handler to support renegotiation offers
    - Check for isRenegotiation flag in offer messages
    - Relay renegotiation offers to target peer
    - Log renegotiation events for debugging
    - _Requirements: 6.2, 7.4_
  
  - [x] 5.2 Implement signaling message retry logic with exponential backoff
    - Wrap message emissions in retry wrapper
    - Retry up to 3 times with exponential backoff (100ms, 200ms, 400ms)
    - Log failed deliveries after exhausting retries
    - _Requirements: 6.3_
  
  - [x] 5.3 Add comprehensive connection event logging
    - Log all peer-joined and peer-left events with timestamps
    - Log offer/answer exchanges with participant identifiers
    - Log ICE candidate exchanges
    - Log connection failures with error details
    - _Requirements: 6.4_
  
  - [x] 5.4 Ensure media state changes trigger proper signaling propagation
    - Verify media-state-changed events are broadcast to all room participants
    - Include audio, video, and screen sharing states in broadcasts
    - Test that state changes trigger renegotiation when needed
    - _Requirements: 6.2_
  
  - [ ]* 5.5 Write property test for peer connection establishment
    - **Property 8: Peer Connection Establishment**
    - **Validates: Requirements 6.1**
    - Generate random participant join sequences
    - Verify signaling server facilitates connections between all pairs
    - Test with varying numbers of participants (1-10)
  
  - [ ]* 5.6 Write property test for signaling message retry
    - **Property 10: Signaling Message Retry**
    - **Validates: Requirements 6.3**
    - Simulate random message delivery failures
    - Verify retry attempts with exponential backoff
    - Confirm failure marking after 3 attempts

- [x] 6. Add UI improvements for multiple student camera display
  - [x] 6.1 Implement grid layout for student camera feeds in tutor view
    - Create CSS grid that adapts to number of participants
    - Display all active student camera streams simultaneously
    - Add participant name labels to each video element
    - Handle dynamic addition/removal of participants
    - _Requirements: 5.3_
  
  - [x] 6.2 Add visual indicators for connection status
    - Show loading spinner while establishing connections
    - Display "Connecting..." status during peer connection setup
    - Show "Reconnecting..." during automatic reconnection attempts
    - Display error icon and message for failed connections
    - _Requirements: 3.3, 5.5_
  
  - [ ]* 6.3 Write property test for multiple student camera display
    - **Property 7: Multiple Student Camera Display**
    - **Validates: Requirements 5.3**
    - Generate random numbers of students with cameras enabled
    - Verify all streams appear in tutor UI
    - Test grid layout adapts correctly

- [x] 7. Implement ICE candidate exchange and connection optimization
  - [x] 7.1 Ensure ICE candidates are exchanged bidirectionally
    - Verify onicecandidate handlers emit candidates to signaling server
    - Confirm signaling server relays candidates to target peers
    - Add null checks for end-of-candidates signal
    - _Requirements: 7.3_
  
  - [x] 7.2 Add ICE connection state monitoring
    - Listen to iceconnectionstatechange events
    - Log state transitions (checking, connected, completed, failed)
    - Trigger reconnection on failed state
    - Display connection quality indicators based on state
    - _Requirements: 6.5_
  
  - [ ]* 7.3 Write property test for ICE candidate exchange
    - **Property 14: ICE Candidate Exchange**
    - **Validates: Requirements 7.3**
    - Simulate peer connection establishment with random network topologies
    - Verify candidates are exchanged until connection established
    - Test with various ICE candidate types (host, srflx, relay)

- [ ] 8. Add comprehensive error handling across all components
  - [x] 8.1 Implement getUserMedia error handling
    - Catch permission denied errors
    - Display user-friendly messages explaining camera/mic access
    - Allow joining with audio-only if video fails
    - Provide instructions for granting permissions
    - _Requirements: 3.3, 5.5_
  
  - [x] 8.2 Implement getDisplayMedia error handling
    - Catch user cancellation errors
    - Handle screen share permission denials
    - Display appropriate error messages
    - Allow continuing lesson without screen share
    - _Requirements: 4.5_
  
  - [x] 8.3 Implement SDP negotiation error handling
    - Wrap setLocalDescription/setRemoteDescription in try-catch
    - Log full SDP content on failures
    - Retry negotiation once on failure
    - Provide "Restart Connection" option if retry fails
    - _Requirements: 7.1, 7.2_
  
  - [ ]* 8.4 Write property test for media stream error handling
    - **Property 5: Media Stream Error Handling**
    - **Validates: Requirements 3.3, 4.5, 5.5**
    - Simulate random media acquisition failures
    - Verify errors are logged with context
    - Verify appropriate UI messages are displayed

- [x] 9. Final checkpoint - Integration testing and verification
  - Ensure all tests pass, ask the user if questions arise.

- [x] 10. Implement student join button with active meeting detection
  - [x] 10.1 Add active meeting tracking to signaling server
    - Create activeMeetings Map to track lessonId -> roomId mappings
    - Store meeting info when tutor joins with lessonId
    - Remove meeting from map when tutor disconnects
    - Add REST endpoint /api/active-meeting/:lessonId to check meeting status
    - _Requirements: Student can only join when tutor has started meeting_
  
  - [x] 10.2 Add checkActiveMeeting method to OnlineLessonService
    - Create method to call signaling server REST endpoint
    - Return active status, roomId, and startedAt timestamp
    - Handle errors gracefully
    - _Requirements: Frontend needs to query active meeting status_
  
  - [x] 10.3 Update LessonViewerComponent to check for active meetings
    - Add activeMeetingRoomId property to track current meeting
    - Add checkingActiveMeeting loading state
    - Implement startActiveMeetingCheck to poll every 10 seconds
    - Implement checkActiveMeeting to query signaling server
    - Clear interval on component destroy
    - _Requirements: Student UI must reflect real-time meeting status_
  
  - [x] 10.4 Update canJoinLesson logic to require active meeting
    - Modify canJoinLesson to check activeMeetingRoomId is not null
    - Keep existing time window validation (15 min before to end time)
    - Only enable join button when both conditions are met
    - _Requirements: Button only enabled when tutor has started meeting_
  
  - [x] 10.5 Update joinLesson to use actual room ID
    - Replace random room ID generation with activeMeetingRoomId
    - Navigate to /join/:roomId with actual tutor's room
    - Pass lessonId as query parameter
    - _Requirements: Students join the same room as tutor_
  
  - [x] 10.6 Update UI to show waiting state
    - Show "Waiting for tutor to start meeting" when no active meeting
    - Show countdown when meeting is active but outside time window
    - Show "Join Lesson" button only when meeting active and in time window
    - _Requirements: Clear user feedback on meeting availability_
  
  - [x] 10.7 Update InstantMeetingComponent to pass lessonId
    - Extract lessonId from query parameters
    - Pass lessonId when emitting join-room event
    - Allow signaling server to track meeting by lessonId
    - _Requirements: Tutor's meeting must be trackable by lessonId_

## Notes

- Tasks marked with `*` are optional and can be skipped for faster MVP
- Each task references specific requirements for traceability
- The implementation uses TypeScript for frontend components and Node.js for signaling server
- WebRTC stream identification relies on track composition rather than timing-dependent flags
- Signaling server enhancements are backward compatible with existing connections
- Time calculations use browser local time consistently to avoid timezone issues
- Active meeting detection uses polling every 10 seconds to provide real-time status updates
