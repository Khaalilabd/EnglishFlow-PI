# WebRTC Additional Fixes - 2026-04-14

## Status Check

### Already Fixed ✅:
1. Unified `description` event (no separate offer/answer handlers)
2. Negotiationneeded debouncing (150ms)
3. `makingOffer` and `ignoreOffer` flags in tutor component
4. ICE candidate buffering with 30-second TTL
5. Two-pass getUserMedia
6. Remote track handling with single MediaStream per peer

### Remaining Issues to Fix:

## Prompt 3 Remaining: Guard Against Own Description
- Need to add check: `if (data.from === this.socket.id) return;` in description handlers
- This prevents processing your own SDP if it somehow gets echoed back

## Prompt 1: Fix Camera "Device in use" Conflict
**Problem**: `getUserMedia` called twice - once for preview, once when joining
**Solution**: Reuse the preview stream when joining instead of calling getUserMedia again

### Changes Needed:
1. In `joinMeeting()`, check if `previewStream` exists
2. If yes, assign `this.localStream = this.previewStream` and set `this.previewStream = null`
3. Only call getUserMedia if preview stream doesn't exist
4. Remove duplicate getUserMedia call in join flow

## Prompt 2: Fix Screen Share Not Showing on Remote Side
**Current Issue**: Screen share track replacement logic exists but may need refinement
**Solution**: Ensure `replaceTrack()` is used correctly and media state is broadcast

### Changes Needed:
1. Verify screen track is properly replaced on all peer connections
2. Ensure `onended` handler is wired immediately after getDisplayMedia
3. Confirm media-state broadcast happens after track replacement

## Prompt 4: Fix Remote Video Elements Not Updating
**Problem**: Video elements may not update when streams arrive
**Solution**: Imperative srcObject setting with setTimeout(0)

### Changes Needed:
1. Never use `[srcObject]` binding in templates
2. Always set `videoElement.srcObject = stream` imperatively
3. Use `setTimeout(() => { ... }, 0)` to ensure DOM exists
4. Call attachment in `ngAfterViewChecked` for pending streams

## Prompt 5: Same-Device Testing (Camera Conflict)
**Problem**: Two tabs on same device both request camera
**Solution**: Add NotReadableError handling and ?noCamera URL parameter

### Changes Needed:
1. Catch `NotReadableError` specifically and fall back to audio-only
2. Add URL parameter support: `?noCamera=true` skips video entirely
3. Show user-friendly warning when camera is in use

## Implementation Order:
1. ✅ Prompt 3 (offer loop) - MOSTLY DONE, add own-description guard
2. Prompt 1 (camera conflict) - CRITICAL
3. Prompt 2 (screen share) - VERIFY/REFINE
4. Prompt 4 (video elements) - IMPORTANT
5. Prompt 5 (same-device testing) - NICE TO HAVE
