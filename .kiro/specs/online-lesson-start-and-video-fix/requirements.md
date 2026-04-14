# Requirements Document

## Introduction

This feature enhances the online lesson functionality in the learning platform by enabling tutors to start lessons early and fixing critical WebRTC video streaming issues. Currently, tutors cannot start lessons before the scheduled time, and there are bidirectional video streaming problems where students cannot see the tutor's camera or screen share, and tutors cannot see student cameras.

## Glossary

- **Online_Lesson**: A scheduled virtual learning session between a tutor and one or more students with defined start and end times
- **Tutor**: An instructor who conducts online lessons and shares video/screen content
- **Student**: A learner who participates in online lessons and views tutor content
- **Start_Lesson_Button**: A UI control that allows the tutor to initiate an online lesson
- **WebRTC_Session**: A real-time communication session enabling video, audio, and screen sharing between participants
- **Camera_Feed**: Live video stream from a participant's camera
- **Screen_Share**: Live stream of a participant's screen content
- **Scheduled_Start_Time**: The planned beginning time of an online lesson
- **Scheduled_End_Time**: The planned ending time of an online lesson
- **Early_Start_Window**: The 15-minute period before the scheduled start time when a lesson can be initiated

## Requirements

### Requirement 1: Early Lesson Start Capability

**User Story:** As a tutor, I want to start an online lesson up to 15 minutes before the scheduled time, so that I can prepare the virtual classroom and ensure everything is working before students join.

#### Acceptance Criteria

1. WHEN the current time is 15 minutes or less before the Scheduled_Start_Time, THE Online_Lesson_System SHALL enable the Start_Lesson_Button
2. WHEN the current time is more than 15 minutes before the Scheduled_Start_Time, THE Online_Lesson_System SHALL disable the Start_Lesson_Button
3. WHEN the Tutor clicks the Start_Lesson_Button during the Early_Start_Window, THE Online_Lesson_System SHALL initiate the WebRTC_Session
4. THE Online_Lesson_System SHALL display the time remaining until the Early_Start_Window begins

### Requirement 2: Lesson Button Availability Duration

**User Story:** As a tutor, I want the start lesson button to remain available throughout the entire lesson duration, so that I can restart the session if technical issues occur.

#### Acceptance Criteria

1. WHILE the current time is between 15 minutes before Scheduled_Start_Time and Scheduled_End_Time, THE Online_Lesson_System SHALL keep the Start_Lesson_Button enabled
2. WHEN the current time exceeds the Scheduled_End_Time, THE Online_Lesson_System SHALL disable the Start_Lesson_Button
3. WHEN the Scheduled_End_Time is reached, THE Online_Lesson_System SHALL display a notification that the lesson time has ended

### Requirement 3: Tutor Video Stream Visibility for Students

**User Story:** As a student, I want to see the tutor's camera feed during an online lesson, so that I can have a more engaging and personal learning experience.

#### Acceptance Criteria

1. WHEN the Tutor enables their Camera_Feed in a WebRTC_Session, THE Online_Lesson_System SHALL transmit the video stream to all connected Students
2. WHEN a Student joins an active WebRTC_Session, THE Online_Lesson_System SHALL display the Tutor's Camera_Feed within 3 seconds
3. IF the Tutor's Camera_Feed fails to transmit, THEN THE Online_Lesson_System SHALL log the error and display a connection status message to the Student
4. THE Online_Lesson_System SHALL maintain the Tutor's Camera_Feed quality at a minimum of 480p resolution

### Requirement 4: Tutor Screen Share Visibility for Students

**User Story:** As a student, I want to see the tutor's shared screen during an online lesson, so that I can follow along with presentations, code examples, and teaching materials.

#### Acceptance Criteria

1. WHEN the Tutor initiates Screen_Share in a WebRTC_Session, THE Online_Lesson_System SHALL transmit the screen content to all connected Students
2. WHEN a Student joins an active WebRTC_Session with ongoing Screen_Share, THE Online_Lesson_System SHALL display the shared screen within 3 seconds
3. THE Online_Lesson_System SHALL render the Screen_Share at a minimum of 720p resolution for readability
4. WHEN the Tutor stops Screen_Share, THE Online_Lesson_System SHALL remove the screen display from all Student views within 2 seconds
5. IF Screen_Share transmission fails, THEN THE Online_Lesson_System SHALL log the error and notify the Tutor

### Requirement 5: Student Camera Visibility for Tutor

**User Story:** As a tutor, I want to see all student camera feeds during an online lesson, so that I can monitor engagement, read body language, and provide a more interactive teaching experience.

#### Acceptance Criteria

1. WHEN a Student enables their Camera_Feed in a WebRTC_Session, THE Online_Lesson_System SHALL transmit the video stream to the Tutor
2. WHEN a Student joins the WebRTC_Session, THE Online_Lesson_System SHALL display their Camera_Feed to the Tutor within 3 seconds
3. THE Online_Lesson_System SHALL display all Student Camera_Feeds simultaneously in a grid or gallery layout
4. WHEN a Student disables their Camera_Feed, THE Online_Lesson_System SHALL update the Tutor's view within 2 seconds
5. IF a Student's Camera_Feed fails to transmit, THEN THE Online_Lesson_System SHALL display a placeholder indicating the connection issue

### Requirement 6: WebRTC Signaling Reliability

**User Story:** As a system administrator, I want reliable WebRTC signaling between all participants, so that video and screen sharing work consistently without connection failures.

#### Acceptance Criteria

1. WHEN a participant joins a WebRTC_Session, THE WebRTC_Signaling_Server SHALL establish peer connections with all existing participants within 5 seconds
2. WHEN a media stream is added or removed, THE WebRTC_Signaling_Server SHALL propagate the signaling messages to all relevant peers
3. IF a signaling message fails to deliver, THEN THE WebRTC_Signaling_Server SHALL retry transmission up to 3 times with exponential backoff
4. THE WebRTC_Signaling_Server SHALL log all connection establishment events and failures for debugging
5. WHEN a peer connection fails, THE Online_Lesson_System SHALL attempt to re-establish the connection automatically

### Requirement 7: Media Stream Negotiation

**User Story:** As a developer, I want proper SDP offer/answer negotiation for all media streams, so that video and screen sharing capabilities are correctly established between peers.

#### Acceptance Criteria

1. WHEN the Tutor shares a Camera_Feed or Screen_Share, THE Online_Lesson_System SHALL create an SDP offer including the media track
2. WHEN a Student receives an SDP offer, THE Online_Lesson_System SHALL generate an SDP answer accepting the media streams
3. THE Online_Lesson_System SHALL exchange ICE candidates between all peers to establish optimal connection paths
4. WHEN renegotiation is needed, THE Online_Lesson_System SHALL create and exchange updated SDP offers and answers
5. THE Online_Lesson_System SHALL handle multiple simultaneous media tracks in a single peer connection
