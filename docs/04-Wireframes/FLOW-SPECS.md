# MusicLinkd — Flow Specs (verbatim, as handed over)

**Provenance.** These are the product specs for Flows 1–24, the Home/Dashboard
IA refactor and the Person Profile, exactly as written and handed over in
chat. Until this file existed they lived **only** in Claude Code session
transcripts under `~/.claude/projects/`, which is why they kept being lost to
context compaction and had to be re-pasted. Recovered and committed on
2026-08-31 so that never happens again.

Reproduced **verbatim**, including the parts that turned out to be wrong or
were later overruled — this is the record of what was asked for, not a tidied
summary. Where a decision superseded something here, the decision is recorded
in `docs/09-Roadmap/` and in the commit history, not by editing this file.

Related, and deliberately separate:

- `MASTER-REGISTRY.md` — the registry of what is actually built (24 flows,
  135 screens, schema types, widgets, taxonomy). Notion is its source of truth.
- `PRODUCT-SPEC.md` — the narrative product spec, repo-primary, covering 4 of
  the 24 flows in depth.
- This file — the raw briefs those two were written against.

**Reading rule that governs how these were built.** A spec section becomes a
*state* unless it needs its own route, back-stack entry or deep link. A
confirmation becomes a *dialog*. A selection becomes a *sheet*. Anything owned
by another flow becomes a *handoff*. Section headings like "SCREEN 20 —
LOADING" are states, not screens; reading them as screens is how a 200-screen
app happens.


---

## Source 1 — session `8affd4fc`, 2026-08-30T06:07:17 (121,466 chars)

1. /frontend-design:frontend-design 
2.  
3. 
4. save thsi so that you can keep tsting 

Firebase Web config
FIREBASE_API_KEY=AIzaSyDDu_21yKuqIq1MzxgUFF2SlEGJOHY8xS4
FIREBASE_AUTH_DOMAIN=musiclinkd-7ae30.firebaseapp.com
FIREBASE_PROJECT_ID=musiclinkd-7ae30
FIREBASE_STORAGE_BUCKET=musiclinkd-7ae30.firebasestorage.app
FIREBASE_MESSAGING_SENDER_ID=846160129456
FIREBASE_APP_ID=1:846160129456:web:c92e521e7f211308f03a75
FIREBASE_MEASUREMENT_ID=G-VQWH600D3Z
Firebase Admin identity
FIREBASE_ADMIN_CLIENT_EMAIL=firebase-adminsdk-fbsvc@musiclinkd-7ae30.iam.gserviceaccount.com
FIREBASE_ADMIN_PROJECT_ID=musiclinkd-7ae30
FIREBASE_ADMIN_CLIENT_ID=113860596768995184310
OAuth endpoints
GOOGLE_AUTH_URI=https://accounts.google.com/o/oauth2/auth
GOOGLE_TOKEN_URI=https://oauth2.googleapis.com/token
GOOGLE_CERTS_URI=https://www.googleapis.com/oauth2/v1/certs

Assuming you have done the entire marketplace flow 7, along with 1,2,3,4,5 ?  let me reshare

2. sharing Flow 1 again

MusicLinkd — FLOW 01: USER AUTHENTICATION & ONBOARDING
Complete UI/UX Design Prompt
Design the complete high-fidelity Android mobile experience for:

MUSICLINKD
FLOW 01 — USER AUTHENTICATION & ONBOARDING

This is a DESIGN-ONLY task.

Do not implement backend logic, APIs, Firebase configuration, database migrations, payment infrastructure, or production authentication.

Design the complete production-ready UX/UI for the entire FLOW 01 journey.

Use the existing MusicLinkd Gestalt Design System as the single source of truth.

Do NOT create a separate authentication design system.

Do NOT redesign the global MusicLinkd navigation.

--------------------------------------------------
PRODUCT CONTEXT
--------------------------------------------------

MusicLinkd is India's professional network and marketplace for the music industry.

It connects:

• Singers
• Musicians
• Producers
• Composers
• Lyricists
• Music Directors
• DJs
• RJs
• Podcasters
• Sound Engineers
• Studios
• Venues
• Music Businesses
• Equipment sellers
• Rental providers
• Music schools
• Collaborators

Authentication is the user's secure entry into the MusicLinkd ecosystem.

The experience should feel:

Premium

Fast

Trustworthy

Professional

Music-industry specific

Minimal

Human

This is NOT a generic SaaS signup form.

This is NOT a banking login.

This is NOT a social-media clone.

--------------------------------------------------
CORE PRINCIPLE
--------------------------------------------------

FLOW 01 should take a user from:

NEW / RETURNING USER

↓

AUTHENTICATE

↓

VERIFY IDENTITY

↓

ESTABLISH ACCOUNT

↓

CHOOSE / CONFIRM PROFESSIONAL CONTEXT

↓

ENTER MUSICLINKD

Authentication and onboarding must remain logically distinct.

Authentication proves:

"Who are you?"

Onboarding establishes:

"How will you use MusicLinkd?"

Do not force unnecessary onboarding before the user can authenticate.

--------------------------------------------------
RELATIONSHIP TO OTHER FLOWS
--------------------------------------------------

This is:

FLOW 01 — USER AUTHENTICATION

FLOW 02 — USER ONBOARDING

is the subsequent onboarding journey.

FLOW 10 — PROFILE MANAGEMENT

owns ongoing profile editing.

FLOW 14 — PUBLIC PROFILE

owns public-facing profile experience.

FLOW 16 — VERIFICATIONS

owns professional verification.

FLOW 13 — NOTIFICATIONS

owns notification delivery.

Do not duplicate these systems.

--------------------------------------------------
AUTHENTICATION METHODS
--------------------------------------------------

Support the authentication methods already supported by the MusicLinkd implementation.

Where configured:

• Email
• Password
• Google
• Phone / OTP
• Other enabled provider

Do not visually promise authentication providers that are not actually configured.

The design must be provider-agnostic where implementation status is unknown.

--------------------------------------------------
DESIGN SYSTEM
--------------------------------------------------

Use the existing MusicLinkd Gestalt Design System.

Use existing:

Typography

Spacing

Colour tokens

Buttons

Inputs

Cards

Icons

Elevation

Radius

Dialogs

Bottom sheets

Loading states

Error states

Navigation

Motion

Do not invent arbitrary authentication-specific visual tokens.

If a reusable pattern is missing:

extend Gestalt.

--------------------------------------------------
BRAND / SPLASH
--------------------------------------------------

The authentication experience begins with the MusicLinkd brand.

Use the approved MusicLinkd logo / app icon.

Do not create a new logo in this flow.

Splash should be minimal.

Logo centered.

Subtle motion may communicate:

Wave

Audio

Connection

Link

Network

Do not use excessive animation.

Splash should transition naturally into authentication.

--------------------------------------------------
SCREEN 01
SPLASH SCREEN
--------------------------------------------------

Show:

MusicLinkd logo

Minimal brand treatment

Optional subtle animation

No unnecessary copy.

Duration should be short.

Transition:

↓

Authentication / Session Check

--------------------------------------------------
SCREEN 02
SESSION CHECK
--------------------------------------------------

Determine whether the user is:

Authenticated

Unauthenticated

Returning

Pending verification

Incomplete onboarding

Do not expose technical terminology.

Possible outcomes:

Authenticated
→ Home / Dashboard

Unauthenticated
→ Welcome

Authenticated but onboarding incomplete
→ FLOW 02

Pending authentication
→ Continue verification

--------------------------------------------------
SCREEN 03
WELCOME
--------------------------------------------------

Headline:

MusicLinkd

Supporting message:

Your professional network for music.

Primary:

Get Started

Secondary:

Sign In

Optional:

Explore MusicLinkd

Do not overload the first screen.

--------------------------------------------------
SCREEN 04
SIGN IN
--------------------------------------------------

Headline:

Welcome back.

Fields:

Email

Password

Actions:

Sign In

Forgot Password

Alternative authentication:

Continue with Google

Continue with Phone

Only show configured providers.

Provide:

Show / Hide Password

Remember session where supported

--------------------------------------------------
SCREEN 05
SIGN UP
--------------------------------------------------

Headline:

Create your MusicLinkd account.

Fields:

Email

Password

Confirm Password

Optional:

Phone

CTA:

Create Account

Alternative:

Continue with Google

Show:

Terms

Privacy

Consent

Do not create a giant registration form.

--------------------------------------------------
SCREEN 06
PHONE AUTHENTICATION
--------------------------------------------------

If phone authentication is enabled:

Enter phone number.

Country selector.

Phone number.

CTA:

Continue

Do not hardcode one country as the only possible country unless product configuration requires it.

--------------------------------------------------
SCREEN 07
OTP VERIFICATION
--------------------------------------------------

Show:

Verification code

6-digit input where configured

Resend code

Countdown

Change phone

CTA:

Verify

Support:

Paste OTP

Auto-fill where supported

Do not make users manually re-enter the phone number.

--------------------------------------------------
SCREEN 08
EMAIL VERIFICATION
--------------------------------------------------

Show:

Check your email.

Explain:

A verification link has been sent.

Actions:

Open Email

Resend

Change Email

Continue once verified

Do not claim verification succeeded until the authentication state confirms it.

--------------------------------------------------
SCREEN 09
FORGOT PASSWORD
--------------------------------------------------

Headline:

Reset your password.

Input:

Email

CTA:

Send Reset Link

Confirmation:

Check your email for instructions.

Actions:

Back to Sign In

--------------------------------------------------
SCREEN 10
RESET PASSWORD
--------------------------------------------------

Fields:

New Password

Confirm Password

Password visibility

Password requirements

CTA:

Update Password

Success:

Password updated.

CTA:

Sign In

--------------------------------------------------
SCREEN 11
PASSWORD REQUIREMENTS
--------------------------------------------------

Show requirements progressively.

Example:

Minimum length

Required character classes if configured

Do not invent security requirements that differ from backend policy.

Use clear validation.

--------------------------------------------------
SCREEN 12
AUTHENTICATION ERROR
--------------------------------------------------

Handle:

Invalid credentials

Account not found

Incorrect OTP

Expired OTP

Email not verified

Provider unavailable

Too many attempts

Network failure

Use human-readable language.

Do not expose backend error codes.

--------------------------------------------------
SCREEN 13
ACCOUNT EXISTS
--------------------------------------------------

If signup attempts to use an existing account:

Message:

An account already exists with this email.

Actions:

Sign In

Reset Password

Use another email

Do not create duplicate accounts.

--------------------------------------------------
SCREEN 14
ACCOUNT CREATION SUCCESS
--------------------------------------------------

Show:

Welcome to MusicLinkd.

Next:

Set up your professional profile.

CTA:

Continue

This transitions to:

FLOW 02 — USER ONBOARDING.

--------------------------------------------------
SCREEN 15
RETURNING USER
--------------------------------------------------

Returning authenticated users should not be forced through onboarding again.

If profile is complete:

→ Home / Dashboard

If onboarding incomplete:

→ Continue FLOW 02

If verification incomplete:

→ Relevant verification state

--------------------------------------------------
SCREEN 16
LOGOUT CONFIRMATION
--------------------------------------------------

If logout is exposed from authentication/session controls:

Confirm:

Log out of MusicLinkd?

Actions:

Log Out

Cancel

Do not destroy account data.

--------------------------------------------------
SCREEN 17
ACCOUNT LOCK / SECURITY STATE
--------------------------------------------------

If authentication is temporarily restricted:

Show:

Account access temporarily restricted.

Provide:

Retry Later

Contact Support

Do not expose security internals.

--------------------------------------------------
SCREEN 18
NETWORK ERROR
--------------------------------------------------

Show:

You're offline.

or:

We couldn't connect to MusicLinkd.

Actions:

Retry

Return

Do not imply successful authentication without confirmation.

--------------------------------------------------
SCREEN 19
MAINTENANCE / SERVICE UNAVAILABLE
--------------------------------------------------

If authentication service is unavailable:

Show:

MusicLinkd is temporarily unavailable.

CTA:

Try Again

Support link where appropriate.

--------------------------------------------------
SCREEN 20
AUTHENTICATION LOADING
--------------------------------------------------

Use existing Gestalt skeleton / loading patterns.

States:

Signing In

Creating Account

Verifying

Checking Session

Sending Code

Sending Reset Link

Do not use arbitrary spinners everywhere.

--------------------------------------------------
SCREEN 21
TERMS / PRIVACY
--------------------------------------------------

Provide accessible links to:

Terms of Service

Privacy Policy

Community Guidelines where applicable

Do not bury legal consent.

Use CMS-managed content where available.

This connects to:

FLOW 22 — CMS PAGES.

--------------------------------------------------
SCREEN 22
DELETE ACCOUNT ENTRY
--------------------------------------------------

If account deletion is exposed from account settings:

Use the existing Settings flow.

Do NOT create an independent account deletion architecture.

Authentication should support the secure re-authentication step where required.

--------------------------------------------------
AUTHENTICATION STATES
--------------------------------------------------

Design all relevant states:

Unauthenticated

Sign In

Sign Up

Email Verification Pending

Phone Verification Pending

OTP Entry

Authenticated

Session Restored

Onboarding Required

Verification Required

Invalid Credentials

Expired OTP

Too Many Attempts

Account Existing

Account Restricted

Network Error

Service Unavailable

Loading

Success

--------------------------------------------------
SECURITY UX
--------------------------------------------------

Never expose:

Passwords

Private keys

Service-account credentials

Firebase Admin credentials

Internal tokens

Access tokens

Database credentials

Secrets

The UI must never require these from the user.

Client-side Firebase configuration may exist in implementation where appropriate.

Admin credentials are server-side only.

--------------------------------------------------
ACCESSIBILITY
--------------------------------------------------

Follow MusicLinkd accessibility standards.

Ensure:

Accessible form labels

Adequate contrast

Large touch targets

Keyboard support where applicable

Password visibility control

OTP accessibility

Error association with fields

Screen-reader support

No colour-only error indication

--------------------------------------------------
MOBILE UX
--------------------------------------------------

Design specifically for Android mobile.

Use:

100dvh

Safe areas

Touch-friendly inputs

Keyboard-aware layouts

Sticky CTA where appropriate

Native-feeling transitions

Bottom sheets where appropriate

Avoid desktop form layouts squeezed into mobile.

--------------------------------------------------
PWA
--------------------------------------------------

The same authentication UI must work as a standalone MusicLinkd PWA.

Support:

Standalone mode

Responsive mobile layout

Safe-area handling

Touch interaction

Persistent session behaviour where supported

Deep-link restoration

If a user opens a protected route while unauthenticated:

authenticate

↓

restore intended destination

Do not always send the user blindly to Home.

--------------------------------------------------
DEEP LINK / SESSION RESTORATION
--------------------------------------------------

Support scenarios such as:

User opens a shared profile.

↓

Authentication required.

↓

User signs in.

↓

Return to shared profile.

Likewise:

Marketplace listing

Opportunity

Booking

Business profile

Portfolio

The authentication flow should preserve the intended destination where technically supported.

--------------------------------------------------
AUTHENTICATION → ONBOARDING
--------------------------------------------------

After successful account creation:

↓

FLOW 02 — USER ONBOARDING

Do not combine the two flows into one giant form.

Authentication:

Identity / account.

Onboarding:

Professional context.

--------------------------------------------------
AUTHENTICATION → DASHBOARD
--------------------------------------------------

Returning authenticated user:

Session valid

↓

FLOW 01 complete

↓

HOME / DASHBOARD

Dashboard is not another authentication flow.

It is the application shell / cross-flow entry point.

--------------------------------------------------
DATA CONTRACT
--------------------------------------------------

Every authentication screen must carry canonical metadata.

Example:

{
  "flow": "FLOW-01",
  "screen": "AUTH-01",
  "screenName": "Welcome",
  "module": "Authentication",
  "route": "/auth",
  "dataSource": [
    "authentication",
    "session"
  ],
  "entities": [
    "user",
    "authSession"
  ],
  "states": [
    "loading",
    "loaded",
    "error",
    "offline"
  ],
  "actions": [
    "signIn",
    "signUp",
    "continueWithProvider",
    "resetPassword"
  ]
}

Use actual MusicLinkd schema/entity names where already defined.

Do not invent production database fields.

--------------------------------------------------
SCHEMA RULE
--------------------------------------------------

Authentication should map to the canonical identity/authentication model.

Potential concepts include:

User

Authentication Provider

Auth Session

Verification State

Onboarding State

Do not create duplicate user identity models.

If something is missing:

EXISTING

COMPUTED

PROPOSED

or

FUTURE

Do not silently invent it.

--------------------------------------------------
ADMIN CONNECTION
--------------------------------------------------

Authentication-related admin capabilities may include:

User access

Account status

Verification status

Suspicious activity

Support cases

Account restrictions

Provider configuration

Do not build a separate authentication admin product.

Use existing MusicLinkd Admin architecture.

--------------------------------------------------
NOTIFICATIONS CONNECTION
--------------------------------------------------

FLOW 13 — Notifications owns notification delivery.

Authentication may trigger:

Email verification

OTP

Password reset

Security alerts

But notification delivery itself does not belong to FLOW 01.

--------------------------------------------------
VERIFICATION CONNECTION
--------------------------------------------------

FLOW 16 — Verifications owns professional verification.

Do not confuse:

ACCOUNT VERIFICATION

with

PROFESSIONAL VERIFICATION.

Account verification:

Email / phone / identity access.

Professional verification:

Industry credibility / business verification.

Keep these distinct.

--------------------------------------------------
SETTINGS CONNECTION
--------------------------------------------------

FLOW 21 — Settings & Support owns:

Password management

Account preferences

Security settings

Account deletion

Support

Authentication provides the secure auth mechanisms.

--------------------------------------------------
MASTER DATA
--------------------------------------------------

Do not collect professional roles, instruments, genres, languages etc. as part of basic authentication unless explicitly required.

Those belong primarily to:

FLOW 02 — User Onboarding

and canonical Master Data.

--------------------------------------------------
SPECIAL COMPONENTS
--------------------------------------------------

Auth Header

Logo

Email Input

Password Input

OTP Input

Phone Input

Provider Button

Password Strength Indicator

Verification State

Error Message

Success State

Loading State

Consent Block

Session Restoration State

Deep Link Restoration State

Use existing Gestalt components.

--------------------------------------------------
MICROINTERACTIONS
--------------------------------------------------

Use restrained motion:

Logo entrance

Input focus

Validation

OTP progression

Button loading

Success transition

Error shake only if appropriate

Session restoration

Do not over-animate authentication.

Authentication should feel fast.

--------------------------------------------------
COPY PRINCIPLES
--------------------------------------------------

Use concise, human language.

Avoid:

"Authentication failed due to invalid credentials."

Prefer:

"That email or password doesn't look right."

Avoid technical Firebase terminology.

Never expose internal implementation details.

--------------------------------------------------
FINAL CUSTOMER JOURNEY
--------------------------------------------------

NEW USER:

Splash
↓
Welcome
↓
Create Account
↓
Email / Phone Verification
↓
Account Created
↓
FLOW 02 — Onboarding

RETURNING USER:

Splash
↓
Session Check
↓
Authenticated
↓
Home / Dashboard

RETURNING UNAUTHENTICATED USER:

Splash
↓
Welcome
↓
Sign In
↓
Verification if required
↓
Restore Destination / Home

PASSWORD RECOVERY:

Sign In
↓
Forgot Password
↓
Email
↓
Reset Link
↓
New Password
↓
Success
↓
Sign In

PHONE AUTH:

Sign In / Sign Up
↓
Phone
↓
OTP
↓
Verified
↓
Continue

--------------------------------------------------
FINAL OUTPUT
--------------------------------------------------

Generate the COMPLETE high-fidelity:

FLOW 01 — USER AUTHENTICATION

Include:

Splash

Session Check

Welcome

Sign In

Sign Up

Email authentication

Phone authentication

OTP

Email verification

Forgot password

Reset password

Account exists

Account creation success

Returning user

Onboarding handoff

Session restoration

Deep-link restoration

Authentication errors

Security states

Account restriction

Network errors

Service unavailable

Loading states

Success states

Terms / Privacy access

All relevant dialogs

All relevant bottom sheets

All relevant navigation states

Use existing MusicLinkd Gestalt.

Use existing MusicLinkd branding.

Use existing authentication capabilities where configured.

Do not invent unsupported providers.

Do not duplicate Onboarding.

Do not duplicate Profile Management.

Do not duplicate Verification.

Do not duplicate Notifications.

Do not duplicate Settings.

Do not create wireframes.

Create polished, production-ready, high-fidelity Android mobile UI.

The experience should feel like the front door to a serious professional music ecosystem.

3. MusicLinkd — FLOW 02: USER ONBOARDING
Complete UI/UX Design Prompt
Design the complete high-fidelity Android mobile experience for:

MUSICLINKD
FLOW 02 — USER ONBOARDING

This is a DESIGN-ONLY task.

Do not implement backend logic, APIs, Firebase, database migrations, payment infrastructure, or production authentication.

Design the complete production-ready UX/UI for the entire FLOW 02 journey.

Use the existing MusicLinkd Gestalt Design System as the single source of truth.

Do NOT create a separate onboarding design system.

Do NOT redesign the global MusicLinkd navigation.

--------------------------------------------------
PRODUCT CONTEXT
--------------------------------------------------

MusicLinkd is India's professional network and marketplace for the music industry.

It connects:

• Singers
• Musicians
• Producers
• Composers
• Lyricists
• Music Directors
• DJs
• RJs
• Podcasters
• Sound Engineers
• Studios
• Venues
• Music Businesses
• Equipment sellers
• Rental providers
• Music schools
• Collaborators

FLOW 01 establishes the user's account.

FLOW 02 establishes the user's professional context inside MusicLinkd.

The onboarding experience should answer:

WHO ARE YOU?

WHAT DO YOU DO?

WHAT ARE YOU GOOD AT?

WHAT MUSIC DO YOU WORK WITH?

WHERE ARE YOU?

WHAT ARE YOU AVAILABLE FOR?

WHAT ARE YOU LOOKING FOR?

WHAT SHOULD MUSICLINKD SHOW YOU?

--------------------------------------------------
CORE PRINCIPLE
--------------------------------------------------

Do NOT turn onboarding into a giant registration form.

Progressively build the user's professional identity.

The journey should feel:

Fast

Personal

Professional

Useful

Optional where appropriate

The user should understand why each piece of information is being requested.

The goal is not:

"Complete a profile."

The goal is:

"Make MusicLinkd useful for you."

--------------------------------------------------
FLOW RELATIONSHIPS
--------------------------------------------------

FLOW 01
User Authentication

↓

FLOW 02
User Onboarding

↓

FLOW 10
Profile Management

↓

FLOW 14
Public Profile

The onboarding system should establish the initial profile data.

Ongoing editing belongs to FLOW 10.

Professional verification belongs to:

FLOW 16 — Verifications

Portfolio creation belongs to:

FLOW 03 — Add Portfolio

Services and availability belong to:

FLOW 11 — Services & Availability

Do NOT duplicate those systems.

--------------------------------------------------
ONBOARDING MODES
--------------------------------------------------

Support:

NEW USER

RETURNING USER WITH INCOMPLETE PROFILE

RETURNING USER WITH PARTIAL PROFILE

PROFILE COMPLETION FROM PROFILE SETTINGS

If onboarding was previously started:

Resume from the last incomplete step.

Do not make users repeat completed information.

--------------------------------------------------
DESIGN SYSTEM
--------------------------------------------------

Use existing MusicLinkd Gestalt.

Use existing:

Typography

Spacing

Colour tokens

Buttons

Inputs

Cards

Chips

Avatars

Progress indicators

Bottom sheets

Dialogs

Selection controls

Media components

Motion

Do not invent arbitrary onboarding-specific tokens.

If a reusable component is missing:

extend Gestalt.

--------------------------------------------------
SCREEN 01
ONBOARDING WELCOME
--------------------------------------------------

Headline:

Let's build your MusicLinkd profile.

Supporting copy:

Tell us a little about what you do in music.

Show lightweight progress:

Step 1 of X

Primary:

Let's Start

Secondary:

Skip for Now

Do not make unnecessary information mandatory.

--------------------------------------------------
SCREEN 02
IDENTITY BASICS
--------------------------------------------------

Collect:

Display Name

Username

Profile Photo

Optional Cover Image

The profile photo should support:

Camera

Gallery

Crop

Replace

Remove

Do not require a professional headshot.

--------------------------------------------------
SCREEN 03
PROFILE PHOTO
--------------------------------------------------

Provide a focused photo selection experience.

Show:

Upload Photo

Take Photo

Choose from Gallery

Use Existing Photo

Crop

Preview

CTA:

Continue

Allow skip if product policy permits.

--------------------------------------------------
SCREEN 04
PRIMARY ROLE
--------------------------------------------------

Question:

What do you do?

Examples:

Singer

Musician

Producer

Composer

Lyricist

Music Director

Sound Engineer

DJ

RJ

Podcaster

Voice Artist

Music Teacher

Other

Allow:

One primary role.

This becomes the foundation for role-specific profile configuration.

--------------------------------------------------
SCREEN 05
SECONDARY ROLES
--------------------------------------------------

Question:

Anything else you do?

Allow multiple roles.

Example:

Musician

Producer

Composer

Do not force users to choose multiple roles.

Primary role remains distinct.

--------------------------------------------------
SCREEN 06
INSTRUMENTS
--------------------------------------------------

Only show when relevant.

Question:

What do you play?

Examples:

Guitar

Bass

Keyboard

Piano

Drums

Tabla

Sitar

Flute

Violin

Percussion

Other

Allow:

Primary Instrument

Additional Instruments

Use canonical MusicLinkd master data.

Do not create a second instrument taxonomy.

--------------------------------------------------
SCREEN 07
PROFESSIONAL SPECIALISATION
--------------------------------------------------

Adapt based on role.

Examples:

Singer:

Vocal type

Vocal range

Performance type

Musician:

Instrument

Session work

Live performance

Producer:

Production

Arrangement

Beat making

Composer:

Film

Advertising

Background score

Sound Engineer:

Recording

Mixing

Mastering

Live Sound

Do not show irrelevant fields.

This is a role-adaptive onboarding system.

--------------------------------------------------
SCREEN 08
GENRES
--------------------------------------------------

Question:

What music do you work with?

Examples:

Bollywood

Classical

Folk

Rock

Indie

Jazz

Fusion

Sufi

Devotional

Electronic

Hip-Hop

Regional

Other

Allow multiple selections.

Use canonical genre data.

--------------------------------------------------
SCREEN 09
LANGUAGES
--------------------------------------------------

Question:

Which languages do you work in?

Examples:

Hindi

Punjabi

Tamil

Telugu

Bengali

Marathi

Gujarati

Kannada

Malayalam

Bhojpuri

English

Other

Allow multiple.

Use canonical MusicLinkd language data.

--------------------------------------------------
SCREEN 10
LOCATION
--------------------------------------------------

Question:

Where are you based?

Fields:

Country

State

City

Optional locality

Support:

Use My Location

Choose Manually

Do not force precise GPS location.

City-level information should be sufficient for most discovery use cases.

--------------------------------------------------
SCREEN 11
WORK MODE
--------------------------------------------------

Question:

How do you work?

Options:

In Person

Remote

Hybrid

Travel / Touring

Multiple selections where applicable.

This helps Discovery and Opportunities.

--------------------------------------------------
SCREEN 12
AVAILABILITY
--------------------------------------------------

Question:

What are you available for?

Examples:

Shows

Session Work

Collaborations

Projects

Teaching

Recording

Production

Freelance Work

Touring

Use canonical Services & Availability architecture.

Do not create a second availability system.

--------------------------------------------------
SCREEN 13
WHAT ARE YOU LOOKING FOR?
--------------------------------------------------

Question:

What brings you to MusicLinkd?

Options:

Find Collaborators

Find Work

Hire Talent

Find Studios

Buy Equipment

Rent Equipment

Sell Equipment

Join Communities

Discover Music

Promote My Work

Build Professional Network

Allow multiple.

These preferences can influence Discovery and Recommendations.

--------------------------------------------------
SCREEN 14
PROFESSIONAL EXPERIENCE
--------------------------------------------------

Question:

How experienced are you?

Options may include:

Just Starting

1–2 Years

3–5 Years

5–10 Years

10+ Years

Professional / Established

Avoid making experience level feel like a ranking.

Use it primarily for relevance and profile context.

--------------------------------------------------
SCREEN 15
BIO
--------------------------------------------------

Prompt:

Tell people about yourself.

Provide concise guidance.

Example placeholder:

Singer and songwriter based in Mumbai, working across indie, Bollywood and live performance.

Character limit.

Show:

Character count

Preview

Do not require a long biography.

--------------------------------------------------
SCREEN 16
PROFILE LINKS
--------------------------------------------------

Optional external links:

Website

Instagram

YouTube

Spotify

SoundCloud

Other supported platforms

Do not force every platform.

Validate URLs.

--------------------------------------------------
SCREEN 17
CONTACT PREFERENCES
--------------------------------------------------

Allow users to choose how they can be contacted.

Options:

MusicLinkd Messages

Email

Phone

Business Contact

Keep contact details private by default unless explicitly shared.

Do not expose private contact information through public profiles without permission.

--------------------------------------------------
SCREEN 18
DISCOVERY PREFERENCES
--------------------------------------------------

Question:

What would you like to discover?

Options:

People

Studios

Services

Music

Projects

Opportunities

Marketplace

Communities

Businesses

This feeds FLOW 04 and FLOW 20.

Do not create a duplicate recommendation system.

--------------------------------------------------
SCREEN 19
PROFILE VISIBILITY
--------------------------------------------------

Choose:

Public

MusicLinkd Members

Limited

Explain consequences clearly.

Avoid complicated privacy terminology.

Provide:

Preview Public Profile

--------------------------------------------------
SCREEN 20
PROFILE PREVIEW
--------------------------------------------------

Show the user's initial public profile.

Include:

Profile Photo

Name

Primary Role

Location

Genres

Instruments

Bio

Availability

Links

Selected profile information

CTA:

Complete Profile

Secondary:

Edit

This should reuse FLOW 14's public profile architecture.

Do not build a separate preview system.

--------------------------------------------------
SCREEN 21
PROFILE COMPLETION
--------------------------------------------------

Show a useful completion indicator.

Example:

Your profile is 70% ready.

Missing:

Portfolio

Services

Availability

Verification

Do NOT force all of these into onboarding.

Provide:

Add Portfolio

Set Services

Set Availability

Verify Profile

Continue to MusicLinkd

Each links to its canonical flow.

--------------------------------------------------
SCREEN 22
ONBOARDING COMPLETE
--------------------------------------------------

Show:

You're ready for MusicLinkd.

Primary:

Explore MusicLinkd

Secondary:

View My Profile

Possible contextual recommendation:

Complete Portfolio

Do not block entry to the app unless required.

--------------------------------------------------
SCREEN 23
SKIP / COMPLETE LATER
--------------------------------------------------

If user skips:

Explain:

You can complete your profile anytime.

Actions:

Continue to MusicLinkd

Continue Setup

Do not punish incomplete profiles.

--------------------------------------------------
SCREEN 24
RESUME ONBOARDING
--------------------------------------------------

Returning user:

Welcome back.

Show:

You've already completed X of Y steps.

CTA:

Continue Setup

Secondary:

Go to MusicLinkd

Resume from the last incomplete meaningful step.

--------------------------------------------------
SCREEN 25
ROLE-SPECIFIC ONBOARDING
--------------------------------------------------

The onboarding must adapt.

Example:

SINGER

Role

Vocal Type

Genres

Languages

Performance

Availability

Portfolio prompt

MUSICIAN

Role

Instrument

Secondary Instruments

Genres

Experience

Availability

PRODUCER

Role

Production Skills

Genres

DAW / Tools if supported

Experience

Services

COMPOSER

Role

Composition Type

Genres

Projects

Languages

Availability

ENGINEER

Role

Recording / Mixing / Mastering

DAW

Equipment / Expertise

Services

Availability

DJ

Role

Genres

Event Types

Equipment

Availability

Do not create completely separate onboarding flows.

Use conditional sections inside one onboarding architecture.

--------------------------------------------------
SCREEN 26
BUSINESS INTENT
--------------------------------------------------

If user indicates they represent a business:

Ask:

What type of business?

Examples:

Studio

Label

Agency

Venue

Dealer

Rental Provider

Music School

Production House

Other

This should hand off to:

FLOW 12 — Business Profiles.

Do not create a duplicate Business Profile inside User Onboarding.

--------------------------------------------------
SCREEN 27
BAND / CREW INTENT
--------------------------------------------------

If user indicates they are part of a band or crew:

Option:

Create / Join Band

This hands off to:

FLOW 15 — Band.

Do not create the complete Band system inside onboarding.

--------------------------------------------------
SCREEN 28
VERIFICATION PROMPT
--------------------------------------------------

After profile basics:

Show:

Want to build more trust?

Optional:

Verify your professional identity.

CTA:

Verify

Secondary:

Later

This links to:

FLOW 16 — Verifications.

Do not force professional verification during basic onboarding unless required by product policy.

--------------------------------------------------
SCREEN 29
PORTFOLIO PROMPT
--------------------------------------------------

After professional identity:

Show:

Show your work.

CTA:

Add Portfolio

Secondary:

Later

This links to:

FLOW 03 — Add Portfolio.

Do not recreate portfolio upload architecture here.

--------------------------------------------------
SCREEN 30
SERVICES PROMPT
--------------------------------------------------

For professionals offering services:

Set up your services.

CTA:

Add Services

Secondary:

Later

Links to:

FLOW 11 — Services & Availability.

--------------------------------------------------
SCREEN 31
ONBOARDING ERROR
--------------------------------------------------

Handle:

Save failed

Image upload failed

Network unavailable

Invalid data

Session expired

Use human-readable error messages.

Do not expose backend errors.

--------------------------------------------------
SCREEN 32
OFFLINE
--------------------------------------------------

If offline:

Allow local progression only where safely supported.

Clearly indicate:

Your changes will sync when you're back online.

Do not falsely imply data has been saved remotely.

--------------------------------------------------
SCREEN 33
LOADING
--------------------------------------------------

Use existing Gestalt loading patterns.

States:

Saving

Uploading

Loading master data

Preparing profile

Syncing profile

--------------------------------------------------
SCREEN 34
EMPTY / NO SELECTION
--------------------------------------------------

Where selection is optional:

Do not show an error.

Instead:

Skip

Choose Later

Continue

Avoid artificial validation.

--------------------------------------------------
SCREEN 35
VALIDATION
--------------------------------------------------

Validate:

Username

Required identity fields

URLs

Character limits

Location

Role

Use inline validation.

Do not wait until the final screen to reveal every error.

--------------------------------------------------
PROGRESS MODEL
--------------------------------------------------

Do not use a rigid 20-screen wizard if information can be intelligently grouped.

Recommended conceptual progression:

IDENTITY
→
PROFESSION
→
EXPERTISE
→
LOCATION
→
AVAILABILITY
→
INTENT
→
PROFILE
→
PERSONALISATION
→
PREVIEW
→
MUSICLINKD

Use progressive disclosure.

The actual screen count may vary based on role.

--------------------------------------------------
SMART SKIP
--------------------------------------------------

If a field is irrelevant:

Do not show it.

If information is already known:

Do not ask again.

If information can be inferred safely from existing account data:

prefill it and allow editing.

If information belongs to another flow:

link to that flow.

--------------------------------------------------
SCHEMA RULE
--------------------------------------------------

Onboarding writes to the canonical user/profile model.

Potential data domains:

User

Professional Profile

Roles

Instruments

Genres

Languages

Location

Availability

Preferences

Profile Links

Visibility

Do not create duplicate entities.

Use canonical MusicLinkd master data.

If a field is not currently supported:

mark it:

EXISTING

COMPUTED

PROPOSED

or

FUTURE.

Do not silently invent production schema.

--------------------------------------------------
SCREEN DATA CONTRACT
--------------------------------------------------

Every onboarding screen must contain canonical metadata.

Example:

{
  "flow": "FLOW-02",
  "screen": "ONB-01",
  "screenName": "Onboarding Welcome",
  "module": "Onboarding",
  "route": "/onboarding",
  "dataSource": [
    "users",
    "professionalProfiles",
    "masterData"
  ],
  "entities": [
    "user",
    "professionalProfile",
    "role",
    "instrument",
    "genre",
    "language",
    "location",
    "availability",
    "preferences"
  ],
  "states": [
    "loading",
    "loaded",
    "saving",
    "error",
    "offline"
  ],
  "actions": [
    "continue",
    "skip",
    "save",
    "back",
    "complete"
  ]
}

Use actual MusicLinkd schema names wherever already defined.

--------------------------------------------------
ADMIN CONNECTION
--------------------------------------------------

Onboarding-related admin capabilities may include:

Master data

Roles

Instruments

Genres

Languages

Location data

Profile moderation

Profile completion analytics

Reported profiles

Verification

Do not create a separate onboarding admin product.

--------------------------------------------------
NOTIFICATIONS CONNECTION
--------------------------------------------------

FLOW 13 owns notifications.

Onboarding may trigger:

Welcome notification

Profile completion reminder

Verification reminder

Portfolio reminder

But notification delivery belongs to FLOW 13.

--------------------------------------------------
RECOMMENDATION CONNECTION
--------------------------------------------------

FLOW 20 owns recommendation logic.

Onboarding supplies signals such as:

Role

Instrument

Genre

Language

Location

Interests

Availability

Do not build recommendation logic here.

--------------------------------------------------
DISCOVERY CONNECTION
--------------------------------------------------

FLOW 04 consumes onboarding signals to improve discovery.

Example:

Musician

+

Guitar

+

Indie

+

Mumbai

↓

Relevant people

Studios

Opportunities

Services

Music

--------------------------------------------------
OPPORTUNITIES CONNECTION
--------------------------------------------------

FLOW 05 consumes:

Role

Skills

Experience

Location

Availability

Intent

Portfolio

Do not duplicate Opportunity profile requirements.

--------------------------------------------------
MARKETPLACE CONNECTION
--------------------------------------------------

FLOW 07 may consume:

Location

Professional role

Equipment interests

Buy / Sell / Rent intent

Do not create marketplace onboarding inside this flow.

--------------------------------------------------
BAND CONNECTION
--------------------------------------------------

FLOW 15 handles:

Band creation

Band membership

Band profile

Onboarding only identifies intent and hands off.

--------------------------------------------------
BUSINESS CONNECTION
--------------------------------------------------

FLOW 12 handles:

Business identity

Business profile

Services

Team

Business verification

Onboarding only establishes whether the user wants to create / represent a business.

--------------------------------------------------
VERIFICATION CONNECTION
--------------------------------------------------

FLOW 16 owns verification.

Onboarding may recommend verification.

It does not execute the complete verification workflow.

--------------------------------------------------
PORTFOLIO CONNECTION
--------------------------------------------------

FLOW 03 owns portfolio.

Onboarding should encourage adding work after professional identity is established.

--------------------------------------------------
SERVICES CONNECTION
--------------------------------------------------

FLOW 11 owns:

Services

Pricing

Availability

Onboarding may collect high-level intent but should not create full service configuration.

--------------------------------------------------
PROFILE COMPLETION
--------------------------------------------------

Profile completion should be based on usefulness, not arbitrary percentage.

Examples:

Identity complete

Professional context complete

Discovery-ready

Opportunity-ready

Portfolio-ready

Business-ready

Do not make "100%" mandatory for app access.

--------------------------------------------------
MICROINTERACTIONS
--------------------------------------------------

Use restrained motion:

Progress

Selection

Chip selection

Photo upload

Save

Validation

Step transitions

Profile preview

Completion

Avoid excessive onboarding animation.

--------------------------------------------------
ACCESSIBILITY
--------------------------------------------------

Follow MusicLinkd accessibility standards.

Ensure:

Accessible labels

Large touch targets

Keyboard-aware forms

Clear selected states

Accessible chips

Screen-reader support

No colour-only status

--------------------------------------------------
ANDROID / PWA
--------------------------------------------------

Design mobile-first.

Support:

100dvh

Safe areas

Touch interaction

Keyboard-aware scrolling

Native-feeling transitions

PWA standalone mode

Persistent progress

Deep-link recovery

--------------------------------------------------
ABANDONMENT / RECOVERY
--------------------------------------------------

If the user exits onboarding:

Persist completed steps.

On return:

Resume.

Never discard already completed information.

--------------------------------------------------
FINAL CUSTOMER JOURNEY
--------------------------------------------------

FLOW 01

Authenticate

↓

FLOW 02

ONBOARD

↓

Identity

↓

Professional Role

↓

Expertise

↓

Genres / Languages

↓

Location

↓

Availability / Intent

↓

Profile Basics

↓

Personalisation

↓

Preview

↓

MusicLinkd

↓

FLOW 04 — Discover

FLOW 05 — Opportunities

FLOW 03 — Add Portfolio

FLOW 11 — Services

FLOW 16 — Verification

FLOW 15 — Band

FLOW 12 — Business

as relevant.

--------------------------------------------------
FINAL OUTPUT
--------------------------------------------------

Generate the COMPLETE high-fidelity:

FLOW 02 — USER ONBOARDING

Include:

Onboarding Welcome

Identity

Profile Photo

Primary Role

Secondary Roles

Instruments

Role-specific Expertise

Genres

Languages

Location

Work Mode

Availability Intent

Professional Intent

Experience

Bio

Profile Links

Contact Preferences

Discovery Preferences

Visibility

Profile Preview

Profile Completion

Onboarding Complete

Skip

Resume

Role-specific states

Business Intent

Band Intent

Verification Prompt

Portfolio Prompt

Services Prompt

Validation

Loading

Saving

Offline

Error

All relevant bottom sheets

All relevant dialogs

All relevant navigation states

Use existing MusicLinkd Gestalt.

Use existing MusicLinkd master data.

Use existing Profile, Portfolio, Services, Availability, Business, Band, Verification, Discover, Opportunities and Recommendation systems.

Do not duplicate functionality owned by another flow.

Do not create wireframes.

Do not create a generic social-network onboarding.

Do not create a generic SaaS signup wizard.

Create a premium, intelligent, progressive onboarding experience specifically for MusicLinkd.

The user should finish onboarding feeling:

"I've told MusicLinkd enough to understand me."

Not:

"I've filled out a 30-page form."

   4. MusicLinkd — FLOW 03: ADD PORTFOLIO
Complete UI/UX Design Prompt
Design the complete high-fidelity Android mobile experience for:

MUSICLINKD
FLOW 03 — ADD PORTFOLIO

This is a DESIGN-ONLY task.

Do not implement backend logic, APIs, Firebase, database migrations, payment infrastructure, or production authentication.

Design the complete production-ready UX/UI for the entire FLOW 03 journey.

Use the existing MusicLinkd Gestalt Design System as the single source of truth.

Do NOT create a separate Portfolio design system.

Do NOT redesign global MusicLinkd navigation.

--------------------------------------------------
PRODUCT CONTEXT
--------------------------------------------------

MusicLinkd is a professional network and marketplace built specifically for the music industry.

A professional profile tells people:

WHO YOU ARE.

A portfolio proves:

WHAT YOU HAVE DONE.

FLOW 03 is therefore the professional proof-of-work layer.

Portfolio may contain:

• Audio
• Tracks
• Songs
• Albums
• Videos
• Performances
• Images
• Projects
• Credits
• Discography
• Writing samples
• Production work
• Mixing / mastering work
• Live work
• Client work
• Case studies
• External work links

The portfolio must feel like a professional showcase, not a social-media gallery.

--------------------------------------------------
CORE PRINCIPLE
--------------------------------------------------

The journey is:

ADD WORK
↓
DESCRIBE WORK
↓
CLASSIFY WORK
↓
ADD MEDIA
↓
ADD CREDITS
↓
PREVIEW
↓
PUBLISH
↓
SHOWCASE
↓
SHARE
↓
UPDATE / MANAGE

The user should be able to create a useful portfolio quickly.

Do not force every optional field.

--------------------------------------------------
RELATIONSHIP TO OTHER FLOWS
--------------------------------------------------

FLOW 01
Authentication

FLOW 02
Onboarding

FLOW 03
Add Portfolio

FLOW 04
Discover

FLOW 05
Opportunities

FLOW 06
Communication & Collaboration

FLOW 07
Marketplace

FLOW 08
Booking

FLOW 10
Profile Management

FLOW 11
Services & Availability

FLOW 12
Business Profiles

FLOW 14
Public Profile

FLOW 15
Band

FLOW 16
Verifications

FLOW 17
Reviews

FLOW 20
Recommendation Engine

Portfolio is referenced by many flows.

Do NOT duplicate portfolio content elsewhere.

--------------------------------------------------
PORTFOLIO MODEL
--------------------------------------------------

Portfolio contains:

PORTFOLIO

↓

WORK ITEMS

↓

MEDIA / PROJECT DATA

↓

CREDITS / CONTRIBUTORS

↓

METADATA

↓

VISIBILITY

A work item may represent:

Track

Album

Video

Performance

Project

Client Work

Case Study

Credit

Writing Sample

Image

Other supported work.

--------------------------------------------------
DESIGN SYSTEM
--------------------------------------------------

Use existing MusicLinkd Gestalt.

Use existing:

Typography

Spacing

Colour tokens

Buttons

Cards

Inputs

Chips

Media components

Audio player

Video player

Image gallery

Avatars

Badges

Dialogs

Bottom sheets

Navigation

Motion

Do not invent arbitrary visual tokens.

If a reusable portfolio component is genuinely missing:

extend Gestalt.

--------------------------------------------------
SCREEN 01
PORTFOLIO ENTRY
--------------------------------------------------

Entry points:

Profile

Onboarding

Dashboard

Opportunity application

Public profile

Portfolio management

Show:

Your Portfolio

Supporting copy:

Show the work you're proud of.

Primary:

Add Work

Secondary:

Manage Portfolio

--------------------------------------------------
SCREEN 02
PORTFOLIO OVERVIEW
--------------------------------------------------

Show:

Portfolio title

Profile owner

Visibility

Work count

Featured work

Recent work

Categories

Primary CTA:

Add Work

Actions:

Edit

Reorder

Preview

Share

Settings

--------------------------------------------------
SCREEN 03
ADD WORK
--------------------------------------------------

Question:

What would you like to add?

Options:

Track

Album

Video

Performance

Project

Client Work

Case Study

Writing Sample

Image

External Link

Other

Use contextual cards.

Do not display irrelevant fields after selection.

--------------------------------------------------
SCREEN 04
WORK TYPE
--------------------------------------------------

Once selected:

Show the appropriate creation form.

Example:

Track

↓

Title

Artist

Role

Genre

Release date

Media

Credits

Description

Example:

Project

↓

Project name

Role

Client

Year

Description

Media

Credits

--------------------------------------------------
SCREEN 05
WORK BASICS
--------------------------------------------------

Fields:

Title

Description

Work type

Year

Genre

Language

Role

Optional:

Client

Organisation

Project status

Use canonical MusicLinkd master data.

--------------------------------------------------
SCREEN 06
ROLE / CREDIT
--------------------------------------------------

Question:

What was your role?

Examples:

Artist

Singer

Composer

Producer

Lyricist

Music Director

Guitarist

Keyboardist

Drummer

Mixing Engineer

Mastering Engineer

Sound Designer

DJ

Director

Other

Allow multiple credits where appropriate.

--------------------------------------------------
SCREEN 07
CONTRIBUTORS
--------------------------------------------------

Allow contributors:

Artist

Musician

Producer

Engineer

Lyricist

Composer

Studio

Business

Other

Search existing MusicLinkd profiles first.

Actions:

Add Contributor

Invite Contributor

Remove

Do not create duplicate user records.

--------------------------------------------------
SCREEN 08
MEDIA UPLOAD
--------------------------------------------------

Allow supported media:

Audio

Video

Images

Documents where appropriate

External links

Actions:

Upload

Choose from existing media

Add Link

Preview

Remove

Do not duplicate storage systems.

--------------------------------------------------
SCREEN 09
AUDIO WORK
--------------------------------------------------

For audio:

Track title

Artist

Role

Genre

Language

Duration

Artwork

Audio file

Credits

Release date

Description

External links

Provide:

Audio preview

Play

Pause

Seek

Volume

--------------------------------------------------
SCREEN 10
VIDEO WORK
--------------------------------------------------

For video:

Title

Role

Description

Thumbnail

Video

Credits

Year

External URL where supported

Provide:

Video preview

Play

Pause

Fullscreen

--------------------------------------------------
SCREEN 11
IMAGE WORK
--------------------------------------------------

Allow:

Single image

Gallery

Project images

Behind-the-scenes

Performance images

Provide:

Upload

Crop

Reorder

Caption

Alt text where appropriate

--------------------------------------------------
SCREEN 12
EXTERNAL WORK
--------------------------------------------------

Allow supported external sources:

YouTube

Spotify

SoundCloud

Website

Other configured platforms

Show:

URL

Preview

Title

Description

Platform

Do not claim an external integration exists unless configured.

--------------------------------------------------
SCREEN 13
PROJECT DESCRIPTION
--------------------------------------------------

Provide:

Overview

Your role

Contribution

Outcome

Credits

Optional:

Client

Year

Genre

Language

This should support professional storytelling without forcing a case-study format for every item.

--------------------------------------------------
SCREEN 14
PORTFOLIO METADATA
--------------------------------------------------

Metadata may include:

Genre

Language

Role

Instrument

Year

Location

Project type

Client

Credits

Tags

Use canonical master data.

Do not create duplicate taxonomies.

--------------------------------------------------
SCREEN 15
VISIBILITY
--------------------------------------------------

Choose:

Public

MusicLinkd Members

Private

Explain:

Public

Visible on your public profile.

Members

Visible to signed-in MusicLinkd users.

Private

Only you can see it.

Where supported, allow portfolio-item visibility independent of overall profile visibility.

--------------------------------------------------
SCREEN 16
FEATURED WORK
--------------------------------------------------

Allow user to mark:

Featured

This item may appear prominently on the profile.

Do not allow unlimited featured content.

Use product rules for maximum featured items.

--------------------------------------------------
SCREEN 17
PORTFOLIO PREVIEW
--------------------------------------------------

Show exactly how the work will appear.

Preview:

Cover / artwork

Title

Type

Role

Description

Media

Credits

Metadata

Actions:

Edit

Publish

--------------------------------------------------
SCREEN 18
PUBLISH
--------------------------------------------------

Confirmation:

Ready to publish?

Show:

Visibility

Media

Credits

Metadata

CTA:

Publish

Secondary:

Save Draft

--------------------------------------------------
SCREEN 19
PUBLISHED
--------------------------------------------------

Show:

Published successfully.

Actions:

View Portfolio

View Work

Share

Add Another Work

Do not redirect users away from the portfolio context unnecessarily.

--------------------------------------------------
SCREEN 20
DRAFT
--------------------------------------------------

Show:

Draft saved.

Actions:

Continue Editing

Preview

Delete Draft

--------------------------------------------------
SCREEN 21
EDIT WORK
--------------------------------------------------

Allow editing:

Title

Description

Media

Credits

Metadata

Visibility

Featured state

External links

Changes should update the canonical work item.

--------------------------------------------------
SCREEN 22
REORDER PORTFOLIO
--------------------------------------------------

Allow:

Drag to reorder

Set featured item

Move item

Save order

Use native-feeling touch interaction.

Do not change underlying data unnecessarily just to support presentation ordering.

--------------------------------------------------
SCREEN 23
PORTFOLIO SETTINGS
--------------------------------------------------

Settings:

Portfolio visibility

Default ordering

Featured work

Display preferences

External links

Media preferences

Do not duplicate Profile Settings.

Only portfolio-specific controls belong here.

--------------------------------------------------
SCREEN 24
DELETE WORK
--------------------------------------------------

Confirmation:

Delete this work?

Explain:

This removes the work from your portfolio.

Actions:

Delete

Cancel

If media is referenced elsewhere, do not imply that all underlying media is permanently deleted unless that is actually the system behaviour.

--------------------------------------------------
SCREEN 25
ARCHIVE WORK
--------------------------------------------------

If supported:

Archive

Archived work is removed from the active portfolio but retained for later restoration.

Actions:

Archive

Restore

--------------------------------------------------
SCREEN 26
PORTFOLIO SHARING
--------------------------------------------------

Allow:

Share Portfolio

Share Work

Copy Link

QR

Message

Use:

FLOW 23 — QR Code & Profile Sharing

for QR functionality.

Do not create a separate QR system.

--------------------------------------------------
SCREEN 27
SHARE WORK
--------------------------------------------------

Preview:

Artwork

Title

Creator

Role

Description

Link

Actions:

Share

Copy Link

Send in MusicLinkd

--------------------------------------------------
SCREEN 28
PORTFOLIO USED IN OPPORTUNITY
--------------------------------------------------

FLOW 05 may request portfolio items.

Show:

Select relevant work

Allow:

Tracks

Projects

Videos

Images

Credits

Profile

Preview selected items.

Do not duplicate portfolio storage.

--------------------------------------------------
SCREEN 29
PORTFOLIO USED IN MESSAGING
--------------------------------------------------

FLOW 06 may allow:

Share Portfolio

Share Project

Share Track

Share Work

These should reference canonical portfolio objects.

Do not upload duplicate copies unnecessarily.

--------------------------------------------------
SCREEN 30
PORTFOLIO USED IN PROFILE
--------------------------------------------------

FLOW 14 Public Profile should display:

Featured Work

Recent Work

Portfolio Sections

The public profile consumes Portfolio data.

It does not own portfolio creation.

--------------------------------------------------
SCREEN 31
PORTFOLIO DISCOVERY
--------------------------------------------------

FLOW 04 Discovery may surface:

Featured work

Projects

Tracks

Videos

Artists

But Discovery consumes Portfolio data.

It does not create another portfolio system.

--------------------------------------------------
SCREEN 32
PORTFOLIO ANALYTICS
--------------------------------------------------

Where analytics are supported:

Views

Plays

Video views

Shares

Saves

Profile clicks

External link clicks

Do not create an independent analytics system.

Use canonical analytics.

--------------------------------------------------
SCREEN 33
WORK ANALYTICS
--------------------------------------------------

For supported media:

Views

Plays

Completion

Shares

Clicks

Where available.

Do not invent analytics for unsupported media.

--------------------------------------------------
SCREEN 34
NO PORTFOLIO
--------------------------------------------------

Show:

Your work belongs here.

Supporting copy:

Add your first track, project or performance.

CTA:

Add Work

--------------------------------------------------
SCREEN 35
NO WORK
--------------------------------------------------

For a newly created portfolio:

Empty state with relevant suggestions based on role.

Singer:

Add a performance

Musician:

Add a recording

Producer:

Add a production

Engineer:

Add a mix

Do not force role-specific assumptions when the user's role is unknown.

--------------------------------------------------
SCREEN 36
MEDIA UPLOAD ERROR
--------------------------------------------------

Handle:

Unsupported file

File too large

Upload failed

Network unavailable

Processing failed

Show:

Retry

Replace

Cancel

Do not silently lose uploads.

--------------------------------------------------
SCREEN 37
MEDIA PROCESSING
--------------------------------------------------

Show:

Processing your audio...

or:

Processing your video...

States:

Uploading

Processing

Ready

Failed

Do not show a playable asset until processing is actually complete.

--------------------------------------------------
SCREEN 38
WORK NOT FOUND
--------------------------------------------------

Show:

This work is no longer available.

CTA:

Back to Portfolio

--------------------------------------------------
SCREEN 39
PERMISSION ERROR
--------------------------------------------------

Show:

You don't have permission to edit this work.

CTA:

Back

--------------------------------------------------
SCREEN 40
LOADING
--------------------------------------------------

Use existing Gestalt skeletons.

Support:

Portfolio

Work detail

Media

Contributors

Analytics

--------------------------------------------------
SCREEN 41
OFFLINE
--------------------------------------------------

If offline:

Allow safe local editing only if the implementation supports it.

Otherwise:

You're offline.

Retry

Do not imply publication succeeded.

--------------------------------------------------
SCREEN 42
SAVE CONFLICT
--------------------------------------------------

If the work was modified elsewhere:

This work has changed.

Actions:

Review Changes

Keep Mine

Reload

Do not silently overwrite.

--------------------------------------------------
PORTFOLIO STATES
--------------------------------------------------

Design:

Empty

Draft

Published

Private

Archived

Processing

Error

Deleted

Unavailable

--------------------------------------------------
MEDIA STATES
--------------------------------------------------

Design:

Not Uploaded

Uploading

Processing

Ready

Failed

Unavailable

--------------------------------------------------
SPECIAL COMPONENTS
--------------------------------------------------

Portfolio Header

Work Card

Audio Card

Video Card

Image Card

Project Card

Credit Card

Contributor Picker

Media Uploader

Media Preview

Audio Player

Video Player

Metadata Selector

Visibility Selector

Portfolio Reorder

Featured Work

Portfolio Analytics

Use Gestalt wherever possible.

--------------------------------------------------
AUDIO UX
--------------------------------------------------

Audio is a core MusicLinkd medium.

The audio player should support:

Play

Pause

Seek

Duration

Progress

Artwork

Track title

Artist

Role

Volume where appropriate

The experience should feel closer to Spotify / Apple Music than a generic file attachment.

Do not create a full music streaming platform.

--------------------------------------------------
PORTFOLIO CARD PRINCIPLE
--------------------------------------------------

Every portfolio card should communicate:

WHAT

WHO

ROLE

MEDIA

CONTEXT

Do not make cards purely visual.

--------------------------------------------------
MOBILE UX
--------------------------------------------------

Design specifically for Android mobile.

Use:

100dvh

Safe areas

Touch targets

Native-feeling gestures

Bottom sheets

Sticky actions where useful

Drag-and-drop reorder

Keyboard-aware forms

Do not compress desktop portfolio management into mobile.

--------------------------------------------------
PWA
--------------------------------------------------

The same interface must work as a standalone MusicLinkd PWA.

Support:

Standalone mode

Responsive mobile layout

Touch interaction

Safe areas

Native-feeling transitions

--------------------------------------------------
ACCESSIBILITY
--------------------------------------------------

Follow MusicLinkd accessibility standards.

Ensure:

Accessible media controls

Alt text

Labels

Keyboard support where applicable

Screen-reader support

Adequate contrast

No colour-only status

Accessible drag/reorder alternatives

--------------------------------------------------
SCREEN DATA CONTRACT
--------------------------------------------------

Every Portfolio screen must carry canonical metadata.

Example:

{
  "flow": "FLOW-03",
  "screen": "PORT-01",
  "screenName": "Portfolio Overview",
  "module": "Portfolio",
  "route": "/profile/portfolio",
  "dataSource": [
    "portfolio",
    "portfolioItems",
    "media",
    "credits"
  ],
  "entities": [
    "portfolio",
    "portfolioItem",
    "media",
    "credit",
    "user"
  ],
  "states": [
    "loading",
    "loaded",
    "empty",
    "draft",
    "published",
    "error",
    "offline"
  ],
  "actions": [
    "add",
    "edit",
    "delete",
    "archive",
    "reorder",
    "feature",
    "share",
    "publish"
  ]
}

Use the actual MusicLinkd schema/entity names wherever already defined.

Do not invent production fields.

--------------------------------------------------
SCHEMA RULE
--------------------------------------------------

Portfolio must use the canonical portfolio architecture.

Potential domains:

Portfolio

Portfolio Item

Media

Project

Credit

Contributor

Visibility

External Link

Analytics

Only use entities actually supported by the canonical schema.

If a required concept is missing:

EXISTING

COMPUTED

PROPOSED

or

FUTURE

Do not silently invent schema.

--------------------------------------------------
ADMIN CONNECTION
--------------------------------------------------

Portfolio-related administration may include:

Content moderation

Reported work

Copyright / infringement reports

Media processing

Featured content

Profile content moderation

External links

Abuse

Do not create a separate portfolio admin system.

Use existing MusicLinkd Admin.

--------------------------------------------------
VERIFICATION CONNECTION
--------------------------------------------------

Verified profiles may display verification on portfolio ownership.

Verification belongs to:

FLOW 16 — Verifications.

Portfolio should consume verification state.

--------------------------------------------------
REVIEWS CONNECTION
--------------------------------------------------

Reviews belong to:

FLOW 17 — Reviews.

Do not embed a separate portfolio review system.

--------------------------------------------------
RECOMMENDATION CONNECTION
--------------------------------------------------

FLOW 20 may use portfolio signals for recommendations.

Examples:

Similar skills

Similar genres

Similar projects

Do not implement recommendation logic inside Portfolio.

--------------------------------------------------
DISCOVERY CONNECTION
--------------------------------------------------

FLOW 04 may surface portfolio work.

Portfolio owns the work.

Discovery owns discovery.

--------------------------------------------------
OPPORTUNITIES CONNECTION
--------------------------------------------------

FLOW 05 can request relevant portfolio work during application.

Portfolio owns the work.

Opportunity owns the application.

--------------------------------------------------
MESSAGING CONNECTION
--------------------------------------------------

FLOW 06 can share:

Portfolio

Project

Track

Video

Work

Portfolio owns the content.

Messaging owns the message.

--------------------------------------------------
PROFILE CONNECTION
--------------------------------------------------

FLOW 14 displays portfolio.

FLOW 10 manages profile.

Portfolio owns portfolio content.

--------------------------------------------------
BAND CONNECTION
--------------------------------------------------

FLOW 15 may contain band portfolio / projects.

Where appropriate:

Band → Projects → Portfolio references

Do not duplicate work unnecessarily.

--------------------------------------------------
BUSINESS CONNECTION
--------------------------------------------------

FLOW 12 Business Profiles may showcase:

Projects

Albums

Campaigns

Studio work

Client work

Business portfolio should reuse the same portfolio architecture where appropriate rather than inventing a separate media model.

--------------------------------------------------
MICROINTERACTIONS
--------------------------------------------------

Use restrained motion:

Upload progress

Audio play

Video preview

Save

Publish

Feature

Reorder

Delete

Share

Do not over-animate professional content.

--------------------------------------------------
FINAL CUSTOMER JOURNEY
--------------------------------------------------

ADD PORTFOLIO:

Profile
↓
Portfolio
↓
Add Work
↓
Choose Type
↓
Work Details
↓
Add Media
↓
Credits
↓
Metadata
↓
Visibility
↓
Preview
↓
Publish
↓
Portfolio

EDIT:

Portfolio
↓
Work
↓
Edit
↓
Save
↓
Updated

SHARE:

Portfolio
↓
Share
↓
Profile / Work / Track
↓
Link / QR / Message

OPPORTUNITY:

Opportunity
↓
Apply
↓
Select Portfolio
↓
Select Work
↓
Preview
↓
Submit Application

MESSAGING:

Chat
↓
Share
↓
Portfolio / Project / Track
↓
Message

--------------------------------------------------
FINAL OUTPUT
--------------------------------------------------

Generate the COMPLETE high-fidelity:

FLOW 03 — ADD PORTFOLIO

Include:

Portfolio Entry

Portfolio Overview

Add Work

Work Type

Work Basics

Role / Credits

Contributors

Media Upload

Audio

Video

Images

External Work

Project Description

Metadata

Visibility

Featured Work

Preview

Publish

Published

Draft

Edit

Reorder

Settings

Delete

Archive

Share

Opportunity Portfolio Selection

Messaging Portfolio Sharing

Public Profile Portfolio

Discovery Portfolio

Portfolio Analytics

Work Analytics

Empty States

Upload Errors

Processing States

Permission States

Loading

Offline

Save Conflicts

All relevant bottom sheets

All relevant dialogs

All relevant navigation states

Use existing MusicLinkd Gestalt.

Use existing Profile, Discovery, Opportunities, Messaging, Business, Band, Verification and Analytics systems.

Do not duplicate functionality owned by another flow.

Do not create a generic social-media gallery.

Do not create a generic Behance clone.

Do not create a generic file manager.

Create a premium, music-industry-specific professional portfolio experience.

The final experience should communicate:

WHO I AM
+
WHAT I DO
+
WHAT I HAVE CREATED
+
WHAT I CONTRIBUTED
+
WHY SOMEONE SHOULD WORK WITH ME


   5. FLOW 4 — DISCOVER
Purpose: Help users discover people, music, businesses, studios, services, projects and communities across the MusicLinkd ecosystem.
Discover
 ↓
Explore
 ↓
Search
 ↓
Choose Discovery Type
 ↓
Apply Filters
 ↓
Results
 ↓
View Profile / Business / Studio / Project
 ↓
Listen / Watch / Explore
 ↓
Connect / Follow / Message
 ↓
Invite to Project / Hire / Book / Enquire
Discover screens
      1. Discover Home
      2. Global Search
      3. Search Results
      4. People / Talent
      5. Businesses
      6. Studios & Spaces
      7. Services
      8. Projects / Portfolio
      9. Music / Audio
      10. Communities
      11. Opportunities
      12. Nearby
      13. Filters
      14. Person Profile
      15. Business Profile
      16. Studio Profile
      17. Project Detail
      18. Service Detail
      19. Community Detail
      20. Saved / Following
Discovery dimensions
DISCOVER
│
├── People
│   ├── Singers
│   ├── Musicians
│   ├── Producers
│   ├── Composers
│   ├── Engineers
│   └── DJs
│
├── Businesses
│   ├── Studios
│   ├── Labels
│   ├── Dealers
│   ├── Agencies
│   └── Schools
│
├── Music
│   ├── Tracks
│   ├── Videos
│   └── Projects
│
├── Services
│   ├── Production
│   ├── Mixing
│   ├── Mastering
│   └── Session Work
│
├── Spaces
│   ├── Recording Studios
│   ├── Jam Rooms
│   └── Venues
│
├── Communities
│   ├── Genre
│   ├── Language
│   └── Region
│
└── Opportunities
Core discovery journey
Discover Home
 ↓
"What are you looking for?"
 ↓
People / Music / Services / Businesses / Spaces
 ↓
Search / Filters
 ↓
Results
 ↓
Open entity
 ↓
Explore portfolio / work / services
 ↓
Connect
The important distinction from Market:
Market = I want to buy, sell or rent something.
Discover = I want to find someone/something within the MusicLinkd ecosystem.
MusicLinkd — FLOW 04: DISCOVERY
Complete UI/UX Design Prompt
Design the complete high-fidelity Android mobile experience for:

MUSICLINKD
FLOW 04 — DISCOVERY

This is a DESIGN-ONLY task.

Do not implement backend logic, APIs, Firebase, database migrations, payment infrastructure, or production authentication.

Design the complete production-ready UX/UI for the Discovery journey.

Use the existing MusicLinkd Gestalt Design System as the source of truth.

Do NOT create a separate Discovery design system.

Do NOT redesign the global MusicLinkd navigation.

--------------------------------------------------
PRODUCT CONTEXT
--------------------------------------------------

MusicLinkd is a professional network and marketplace built specifically for the music industry.

Discovery is the system through which users FIND people, businesses, services, spaces, music, projects, communities and relevant opportunities.

Discovery is NOT simply search.

It combines:

EXPLORE
+
SEARCH
+
FILTER
+
RECOMMEND
+
LOCAL DISCOVERY
+
PROFESSIONAL DISCOVERY

The user should be able to move from:

"I don't know what I'm looking for"

to:

"I found exactly the person / business / service / music / opportunity I need."

--------------------------------------------------
CORE PRINCIPLE
--------------------------------------------------

FLOW 04 — DISCOVER answers:

WHO can I work with?

WHAT exists around me?

WHERE can I find it?

WHAT is relevant to me?

WHO should I know?

WHAT should I explore?

The experience should feel like:

Spotify discovery
+
Airbnb exploration
+
Dribbble discovery
+
LinkedIn professional discovery

but designed specifically for the music industry.

--------------------------------------------------
RELATIONSHIP TO OTHER FLOWS
--------------------------------------------------

This is FLOW 04.

Do NOT absorb other flows into Discovery.

FLOW 03
Add Portfolio

FLOW 05
Opportunities

FLOW 06
Communication & Collaboration

FLOW 07
Marketplace

FLOW 08
Booking

FLOW 10
Profile Management

FLOW 11
Services & Availability

FLOW 12
Business Profiles

FLOW 19
Search

FLOW 20
Recommendation Engine

Discovery is the EXPERIENCE layer.

Search and Recommendation are supporting capabilities.

Do not duplicate them.

--------------------------------------------------
DISCOVERY MODEL
--------------------------------------------------

Discovery should expose several entity types:

PEOPLE

BUSINESSES

STUDIOS / SPACES

SERVICES

MUSIC

PROJECTS

COMMUNITIES

OPPORTUNITIES

MARKETPLACE ITEMS

The user should never feel like they are navigating separate disconnected products.

--------------------------------------------------
DESIGN SYSTEM
--------------------------------------------------

Use the existing MusicLinkd Gestalt Design System.

Use existing:

Typography

Spacing

Colour tokens

Radius

Elevation

Cards

Chips

Buttons

Avatars

Badges

Media components

Audio components

Filters

Search components

Bottom sheets

Navigation

Motion

Do not invent arbitrary colours or component styles.

If Discovery requires a reusable component that does not exist:

extend Gestalt.

Do not create a Discovery-only component unless there is a strong reason.

--------------------------------------------------
GLOBAL NAVIGATION
--------------------------------------------------

Use existing MusicLinkd bottom navigation:

Home

Discover

Market

Messages

Profile

Discover is the active destination.

Do not add additional bottom navigation items.

--------------------------------------------------
SCREEN 01
DISCOVER HOME
--------------------------------------------------

Purpose:

Create an exploration-first landing experience.

Header:

Discover

Supporting text:

Find people, music, spaces and opportunities.

Search field:

Search musicians, studios, services...

Primary discovery categories:

People

Music

Businesses

Studios

Services

Communities

Opportunities

Marketplace

--------------------------------------------------
PERSONALISED DISCOVERY
--------------------------------------------------

Show sections such as:

Recommended for You

Based on Your Profile

People You May Know

Trending in Your Area

New on MusicLinkd

Popular Near You

Recently Active

Relevant Opportunities

Do not show all sections simultaneously.

Prioritise relevance.

--------------------------------------------------
SCREEN 02
DISCOVER PEOPLE
--------------------------------------------------

Purpose:

Find music professionals.

Categories:

Singers

Musicians

Producers

Composers

Lyricists

Music Directors

Engineers

DJs

RJs

Podcasters

Session Musicians

Teachers

Other Professionals

Cards:

Profile image

Name

Role

Verification

Location

Genres

Instruments

Availability

Relevant CTA

View Profile

--------------------------------------------------
SCREEN 03
TALENT RESULTS
--------------------------------------------------

Show professional cards.

Example:

Arjun Mehta

Music Producer · Keyboardist

Mumbai

Electronic · Indie · Bollywood

Available for collaboration

Actions:

View Profile

Connect

Message

Do not overcrowd cards.

--------------------------------------------------
SCREEN 04
BUSINESS DISCOVERY
--------------------------------------------------

Find:

Recording Studios

Mixing Studios

Mastering Studios

Jam Rooms

Music Schools

Labels

Agencies

Equipment Dealers

Rental Providers

Venues

Production Houses

Business card:

Logo

Name

Category

Verification

Location

Rating where applicable

Services

Open Profile

--------------------------------------------------
SCREEN 05
STUDIO / SPACE DISCOVERY
--------------------------------------------------

Show:

Recording Studios

Mixing Studios

Mastering Studios

Podcast Studios

Jam Rooms

Rehearsal Rooms

Venues

Cards:

Photography

Name

Location

Facilities

Pricing

Availability

Rating

CTA:

View

Book

Booking belongs to FLOW 08.

--------------------------------------------------
SCREEN 06
SERVICE DISCOVERY
--------------------------------------------------

Find professional services:

Production

Mixing

Mastering

Songwriting

Composition

Session Work

Voice-over

Live Sound

DJ Services

Teaching

Cards:

Service

Provider

Starting price where available

Location / Remote

Availability

Verification

CTA:

View Service

Enquire

--------------------------------------------------
SCREEN 07
MUSIC DISCOVERY
--------------------------------------------------

Explore:

Tracks

Albums

Singles

Performances

Mixes

Demos

Projects

Audio samples

Video

Use MusicLinkd's existing audio/video components.

Do not create a Spotify clone.

--------------------------------------------------
SCREEN 08
PROJECT DISCOVERY
--------------------------------------------------

Show professional work:

Albums

Films

Commercials

Music Videos

Podcasts

Live Projects

Production Projects

Project card:

Cover

Project name

People involved

Role

Genre

Year

View Project

--------------------------------------------------
SCREEN 09
COMMUNITY DISCOVERY
--------------------------------------------------

Discover:

Regional communities

Genre communities

Language communities

Folk communities

Professional communities

Examples:

Mumbai Musicians

Delhi Producers

Punjabi Music

Indie Artists

Classical Vocalists

Show:

Group image

Name

Category

Location

Members

Activity

Join

Community functionality belongs to the Groups / Communities system.

--------------------------------------------------
SCREEN 10
OPPORTUNITY DISCOVERY
--------------------------------------------------

Surface relevant opportunities.

Examples:

Looking for Guitarist

Singer Required

Mixing Engineer Required

Live Gig

Studio Required

Producer Wanted

Do NOT duplicate FLOW 05.

Selecting an opportunity opens:

FLOW 05 — Opportunities.

--------------------------------------------------
SCREEN 11
MARKETPLACE DISCOVERY
--------------------------------------------------

Surface relevant marketplace inventory.

Examples:

Guitars

Keyboards

Microphones

Audio Interfaces

Speakers

DJ Equipment

Rental Equipment

Do NOT create marketplace listing logic here.

Selecting an item opens:

FLOW 07 — Marketplace.

--------------------------------------------------
SCREEN 12
LOCAL DISCOVERY
--------------------------------------------------

Show:

Near You

Nearby Musicians

Nearby Studios

Nearby Services

Nearby Venues

Nearby Marketplace Listings

Nearby Opportunities

Location selector:

Current Location

City

Area

Distance

Use location carefully.

Do not force location permission before it provides clear value.

--------------------------------------------------
SCREEN 13
GLOBAL SEARCH
--------------------------------------------------

Search is a shared platform capability.

Use:

FLOW 19 — Search

Discovery should launch Search rather than creating a duplicate search system.

Search examples:

Guitarist Mumbai

Recording studio Delhi

Punjabi singer

Mixing engineer

Tabla player

Podcast studio

DJ equipment

Session musician

--------------------------------------------------
SCREEN 14
SEARCH SUGGESTIONS
--------------------------------------------------

While typing show:

People

Businesses

Studios

Services

Music

Projects

Communities

Opportunities

Marketplace

Recent Searches

Trending Searches

Do not make search suggestions noisy.

--------------------------------------------------
SCREEN 15
FILTERS
--------------------------------------------------

Filters must adapt to entity type.

People:

Role

Instrument

Genre

Language

Location

Availability

Experience

Verification

Businesses:

Category

Location

Services

Rating

Verification

Studios:

Type

Location

Facilities

Price

Availability

Rating

Services:

Type

Price

Remote

Location

Availability

Opportunities:

Use FLOW 05 filter architecture.

Marketplace:

Use FLOW 07 filter architecture.

Do not create one giant universal filter form.

--------------------------------------------------
SCREEN 16
SORT
--------------------------------------------------

Contextual sorting:

Recommended

Relevant

Nearby

Newest

Popular

Highly Rated

Recently Active

Do not expose algorithmic details unnecessarily.

--------------------------------------------------
SCREEN 17
DISCOVERY DETAIL
--------------------------------------------------

Discovery should always resolve into the canonical entity experience.

Person:

FLOW 10 / FLOW 14

Business:

FLOW 12

Opportunity:

FLOW 05

Marketplace:

FLOW 07

Booking:

FLOW 08

Project:

Portfolio / Project experience

Community:

Groups / Communities

Do not create duplicate detail pages solely for Discovery.

--------------------------------------------------
SCREEN 18
PERSON PROFILE PREVIEW
--------------------------------------------------

Use a compact preview where appropriate.

Show:

Photo

Name

Role

Verification

Location

Genres

Instruments

Availability

Featured work

Actions:

View Profile

Connect

Message

--------------------------------------------------
SCREEN 19
BUSINESS PREVIEW
--------------------------------------------------

Show:

Logo

Name

Category

Verification

Location

Services

Rating

Availability

Actions:

View

Message

Book

Enquire

Actions must adapt to business type.

--------------------------------------------------
SCREEN 20
SAVED DISCOVERY
--------------------------------------------------

Allow users to save:

People

Businesses

Studios

Services

Music

Projects

Communities

Opportunities

Marketplace items

Saved/Favorites belongs to:

FLOW 24 — Saved & Favorites.

Discovery simply surfaces the capability.

--------------------------------------------------
SCREEN 21
RECENTLY VIEWED
--------------------------------------------------

Show:

People

Businesses

Studios

Projects

Services

Music

Keep it lightweight.

Allow:

Open

Remove

Clear History

--------------------------------------------------
SCREEN 22
DISCOVERY PREFERENCES
--------------------------------------------------

Personalisation signals may include:

Roles

Genres

Instruments

Languages

Location

Availability

Services

Professional interests

These should derive from the canonical profile / master data system.

Do not create a second user-preference taxonomy.

--------------------------------------------------
SCREEN 23
RECOMMENDATIONS
--------------------------------------------------

Show:

Recommended People

Recommended Studios

Recommended Services

Recommended Opportunities

Recommended Music

Recommended Communities

Recommendation logic belongs to:

FLOW 20 — Recommendation Engine.

Discovery displays the result.

--------------------------------------------------
SCREEN 24
WHY THIS RESULT
--------------------------------------------------

Where useful:

Based on your instrument

Near you

Similar genre

Shared connections

Looking for collaboration

Recently active

Do not expose opaque algorithmic scoring.

--------------------------------------------------
SCREEN 25
DISCOVERY FEED
--------------------------------------------------

Optional editorial discovery stream.

May contain:

New artists

Trending tracks

New studios

New businesses

Popular projects

Local activity

Relevant opportunities

Avoid turning Discovery into a generic social feed.

The Social Feed remains its own product capability.

--------------------------------------------------
SCREEN 26
FOLLOW / CONNECT
--------------------------------------------------

From discovery cards support:

Follow

Connect

Message

Save

Actions should be contextual.

Do not display every action on every card.

--------------------------------------------------
SCREEN 27
SHARE
--------------------------------------------------

Allow sharing:

Profile

Business

Studio

Project

Service

Music

Opportunity

Marketplace listing

Use the existing sharing architecture.

--------------------------------------------------
SCREEN 28
DISCOVERY ONBOARDING
--------------------------------------------------

If the user has little profile data:

Ask useful preference questions.

Examples:

What do you do?

What instruments do you use?

What genres interest you?

What languages do you work in?

Where are you based?

What are you looking for?

Use existing onboarding/profile data where possible.

Do not create a duplicate profile setup flow.

--------------------------------------------------
SCREEN 29
EMPTY STATE — NO RESULTS
--------------------------------------------------

Message:

Nothing matched your search.

Suggestions:

Clear filters

Try another location

Explore categories

Discover nearby

Do not use generic decorative messaging.

--------------------------------------------------
SCREEN 30
EMPTY STATE — NEW USER
--------------------------------------------------

Message:

Let's build your MusicLinkd discovery.

CTA:

Complete Profile

Secondary:

Explore Music

--------------------------------------------------
SCREEN 31
LOCATION PERMISSION
--------------------------------------------------

Explain value before requesting location.

Example:

Find musicians, studios and opportunities near you.

Actions:

Use My Location

Choose City

Not Now

Do not block Discovery if permission is denied.

--------------------------------------------------
SCREEN 32
LOADING
--------------------------------------------------

Use existing Gestalt skeleton patterns.

Support:

Discovery Home

People

Businesses

Studios

Music

Services

Projects

Local

Recommendations

--------------------------------------------------
SCREEN 33
ERROR
--------------------------------------------------

Handle:

Network unavailable

Search failed

Location unavailable

Entity unavailable

Permission denied

Use existing MusicLinkd error patterns.

--------------------------------------------------
SCREEN 34
OFFLINE
--------------------------------------------------

Where cached content exists:

Allow browsing cached discovery content.

Clearly indicate stale/offline state.

Never imply that live results are current when they are not.

--------------------------------------------------
SPECIAL COMPONENTS
--------------------------------------------------

Discovery Hero

Search Bar

Category Card

Person Card

Business Card

Studio Card

Service Card

Music Card

Project Card

Community Card

Opportunity Card

Marketplace Card

Recommendation Rail

Location Selector

Filter Sheet

Sort Sheet

Entity Preview

Save Action

Share Action

Use existing Gestalt components first.

--------------------------------------------------
DISCOVERY CARD PRINCIPLE
--------------------------------------------------

Every card should answer:

WHAT IS IT?

WHY IS IT RELEVANT?

WHERE IS IT?

CAN I TRUST IT?

WHAT CAN I DO NEXT?

Avoid cards that only show names and images.

--------------------------------------------------
PERSONALISATION
--------------------------------------------------

Discovery should become more useful as MusicLinkd learns:

Profile role

Instruments

Genres

Languages

Location

Availability

Connections

Activity

Saved items

Search behaviour

Previous interactions

Do not expose sensitive inference.

--------------------------------------------------
MOTION
--------------------------------------------------

Use restrained motion:

Card transitions

Filter application

Search suggestions

Save

Follow

Connect

Location switching

Audio preview

Gallery transitions

Do not animate the entire interface.

--------------------------------------------------
ACCESSIBILITY
--------------------------------------------------

Follow MusicLinkd accessibility standards.

Ensure:

Adequate contrast

Touch targets

Screen reader labels

Accessible media controls

Clear status communication

No colour-only meaning

--------------------------------------------------
ANDROID / PWA
--------------------------------------------------

Design mobile-first.

Support:

100dvh

Safe areas

Touch gestures

Native-feeling scrolling

Responsive Android layouts

PWA standalone presentation

Do not design this as a desktop website compressed into mobile.

--------------------------------------------------
SCREEN DATA CONTRACT
--------------------------------------------------

Every Discovery screen must carry canonical metadata.

Example:

{
  "flow": "FLOW-04",
  "screen": "DISC-01",
  "screenName": "Discover Home",
  "module": "Discovery",
  "route": "/discover",
  "dataSource": [
    "users",
    "businessProfiles",
    "studios",
    "services",
    "projects",
    "opportunities",
    "marketplaceListings"
  ],
  "entities": [
    "user",
    "professionalProfile",
    "businessProfile",
    "studio",
    "service",
    "project",
    "opportunity",
    "marketplaceListing"
  ],
  "states": [
    "loading",
    "loaded",
    "empty",
    "error",
    "offline"
  ],
  "actions": [
    "search",
    "filter",
    "save",
    "follow",
    "connect",
    "share",
    "open"
  ]
}

Use the actual canonical MusicLinkd schema names wherever already defined.

Do not invent production fields simply to satisfy the UI.

--------------------------------------------------
SCHEMA RULE
--------------------------------------------------

Discovery consumes canonical data.

It should not create duplicate:

People models

Business models

Portfolio models

Opportunity models

Marketplace models

Studio models

Community models

Search models

Recommendation models

If a Discovery requirement exposes a missing persisted concept:

identify it as:

EXISTING

COMPUTED

PROPOSED

or

FUTURE.

--------------------------------------------------
ADMIN CONNECTION
--------------------------------------------------

Discovery may require administrative control over:

Featured content

Categories

Taxonomy

Reported profiles

Reported content

Featured businesses

Featured opportunities

Location data

Editorial discovery content

Recommendation configuration

Do not create a separate Discovery admin architecture.

Use existing MusicLinkd Admin capabilities.

--------------------------------------------------
MASTER DATA
--------------------------------------------------

Use canonical MusicLinkd master data for:

Roles

Instruments

Genres

Languages

Regions

Services

Business types

Opportunity types

Marketplace categories

Do not create duplicate lists inside Discovery.

--------------------------------------------------
CROSS-FLOW RULE
--------------------------------------------------

Discovery is the entry point.

It should hand users into canonical flows:

Person
→ Profile

Business
→ Business Profile

Opportunity
→ FLOW 05

Message
→ FLOW 06

Marketplace
→ FLOW 07

Booking
→ FLOW 08

Portfolio
→ FLOW 03

Services
→ FLOW 11

Saved
→ FLOW 24

Search
→ FLOW 19

Recommendations
→ FLOW 20

Notifications
→ FLOW 13

This is essential.

Discovery should CONNECT the ecosystem, not duplicate it.

--------------------------------------------------
FINAL EXPERIENCE
--------------------------------------------------

The user should be able to enter MusicLinkd with no specific destination and quickly move through:

EXPLORE
↓
SEARCH
↓
FILTER
↓
DISCOVER
↓
EVALUATE
↓
OPEN CANONICAL ENTITY
↓
CONNECT / FOLLOW / MESSAGE / HIRE / BOOK / APPLY / BUY

The experience should feel intelligent without feeling automated.

Professional without feeling corporate.

Visual without becoming social-media clutter.

Dense enough to be useful.

Airy enough to remain premium.

Music-industry specific.

--------------------------------------------------
FINAL OUTPUT
--------------------------------------------------

Generate the COMPLETE high-fidelity:

FLOW 04 — DISCOVERY

Include:

Discover Home

People Discovery

Talent Results

Business Discovery

Studio Discovery

Service Discovery

Music Discovery

Project Discovery

Community Discovery

Opportunity Discovery

Marketplace Discovery

Local Discovery

Search Integration

Search Suggestions

Filters

Sorting

Entity Previews

Recommendations

Saved Discovery

Recently Viewed

Discovery Preferences

Personalisation

Follow / Connect

Share

Discovery Onboarding

Empty States

Loading States

Error States

Offline States

Location Permission

All relevant bottom sheets

All relevant dialogs

All relevant navigation states

Maintain one cohesive MusicLinkd experience.

Use existing Gestalt.

Use existing MusicLinkd navigation.

Use canonical Profile, Portfolio, Opportunities, Messaging, Marketplace, Booking, Business, Search, Recommendation, Notifications and Saved/Favorites systems.

Do not duplicate functionality owned by another flow.

Do not create wireframes.

Do not create generic social discovery.

Do not create a generic search engine UI.

Create polished, production-ready, high-fidelity Android mobile UI.

FLOW 04 — DISCOVERY
Key architectural rule for this flow: Discovery is the front door, not the owner of everything it discovers. It surfaces entities and hands the user to the canonical flow/module that owns them. That will keep the eventual screen → schema → admin → GitHub mapping clean.

and finally flow 10 :)
   6. MUSICLINKD — PERSON PROFILE
MASTER UI / UX DESIGN PROMPT

Design the complete high-fidelity Android mobile experience for the MusicLinkd PERSON PROFILE.

This is a DESIGN-ONLY task.

Do not implement backend logic, APIs, Firebase, database migrations or payment infrastructure.

Use the existing MusicLinkd Gestalt Design System as the single source of truth.

Do not create a separate profile design system.

The Profile must work with the existing MusicLinkd ecosystem:

Profile
→ Portfolio
→ Discovery
→ Connections
→ Messaging
→ Projects
→ Marketplace
→ Studios
→ Opportunities

The profile is the professional identity layer of MusicLinkd.

It is NOT a generic social-media profile.
It is NOT a LinkedIn clone.
It is NOT an Instagram profile.

It should feel like:

Professional identity
+
Portfolio
+
Music presence
+
Network identity
+
Collaboration profile

--------------------------------------------------
1. PROFILE PURPOSE
--------------------------------------------------

A MusicLinkd person profile should answer five questions immediately:

WHO ARE YOU?

WHAT DO YOU DO?

WHAT DO YOU CREATE?

WHO DO YOU WORK WITH?

CAN I WORK WITH YOU?

The profile should communicate:

• Professional identity
• Role
• Skills
• Instruments
• Genres
• Languages
• Location
• Availability
• Portfolio
• Music
• Projects
• Experience
• Connections
• Verification
• Services
• Opportunities
• Contact / collaboration actions

--------------------------------------------------
2. DESIGN SYSTEM
--------------------------------------------------

Use the existing MusicLinkd Gestalt Design System.

The existing system is authoritative for:

• Typography
• Colour
• Spacing
• Radius
• Elevation
• Cards
• Buttons
• Chips
• Inputs
• Tabs
• Avatars
• Badges
• Bottom sheets
• Navigation
• Icons
• Motion
• Accessibility

Do NOT invent new visual tokens.

Do NOT use generic Material 3 styling where it conflicts with MusicLinkd.

Do NOT use old hardcoded colours from previous concepts.

Do NOT create profile-specific colours unless explicitly supported by the existing design system.

If a profile interaction requires a missing design-system component:

identify the gap and extend Gestalt properly rather than creating a one-off component.

--------------------------------------------------
3. PROFILE TYPES
--------------------------------------------------

The profile system must support different professional identities.

Examples:

Vocal Artist

Musician

Composer

Lyricist

Music Director

Producer

Beat Producer

DJ

Recording Engineer

Mixing Engineer

Mastering Engineer

Sound Designer

Live Sound Engineer

RJ

Podcaster

Voice-over Artist

Session Musician

Music Teacher

Artist Manager

Other Music Professional

The profile layout should adapt to the person's professional role.

Do not create completely separate profile designs.

Use a shared profile architecture with role-specific sections.

--------------------------------------------------
4. PROFILE HEADER
--------------------------------------------------

SCREEN 01 — PERSON PROFILE

Hero section:

Profile Photo

Display Name

Username

Verification Badge

Primary Role

Location

Availability Status

Example:

Arjun Mehta

Music Producer · Mixing Engineer

Mumbai, India

Available for collaboration

Primary actions:

Connect

Message

Secondary:

Follow

Share

More

Do not overload the header with actions.

Prioritise the actions most relevant to the relationship between viewer and profile owner.

--------------------------------------------------
5. PROFILE PHOTO
--------------------------------------------------

Use a strong professional avatar.

Support:

Profile image

Verification indicator

Availability indicator where appropriate

Do not use decorative frames unless defined by Gestalt.

--------------------------------------------------
6. COVER / HERO
--------------------------------------------------

If the existing MusicLinkd profile system supports a cover image:

Use a restrained editorial cover.

Possible content:

Studio

Performance

Instrument

Production environment

Abstract music visual

Do not make the cover more visually dominant than the professional identity.

If the system does not support cover imagery, do not invent it simply to fill space.

--------------------------------------------------
7. PROFESSIONAL IDENTITY
--------------------------------------------------

Immediately below the hero:

Primary role

Secondary roles

Genres

Instruments

Languages

Location

Experience

Availability

Example:

Music Producer
Composer
Keyboardist

Genres:

Electronic · Indie · Bollywood · Ambient

Languages:

Hindi · English · Punjabi

--------------------------------------------------
8. VERIFICATION
--------------------------------------------------

Use the existing MusicLinkd verification system.

Possible states:

Basic

Email Verified

Professional

Industry Verified

Business

Only show meaningful verification states.

Do not turn verification into a collection of decorative badges.

--------------------------------------------------
9. AVAILABILITY
--------------------------------------------------

Support the existing MusicLinkd status system.

Examples:

Available

In Studio

Busy

Available for Shows

Looking for Collaboration

Available for Session Work

Touring

Location

Availability should be visible but subtle.

--------------------------------------------------
10. PROFILE SUMMARY
--------------------------------------------------

SCREEN 02 — ABOUT

Section:

About

Professional biography.

Support:

Short bio

Longer professional description

Experience

Specialisations

Genres

Languages

Location

Website

Social links

Use progressive disclosure.

Do not show a wall of text.

--------------------------------------------------
11. PROFESSIONAL INFORMATION
--------------------------------------------------

SCREEN 03 — PROFESSIONAL DETAILS

Show role-specific information.

Examples:

Singer:

Vocal type

Vocal range

Languages

Genres

Performance experience

Musician:

Primary instrument

Secondary instruments

Years of experience

Genres

Session experience

Producer:

DAW

Genres

Production styles

Discography

Projects

Engineer:

Specialisation

DAW

Studio experience

Equipment

Genres

DJ:

DJ type

Genres

Event experience

Equipment

The structure should adapt based on profile role.

--------------------------------------------------
12. MUSIC IDENTITY
--------------------------------------------------

SCREEN 04 — MUSIC PROFILE

This is a core MusicLinkd differentiator.

Show:

Genres

Instruments

Languages

Musical styles

Influences

Specialisations

Use compact visual chips/cards.

Do not turn this into a generic skill-tag cloud.

--------------------------------------------------
13. AUDIO PORTFOLIO
--------------------------------------------------

SCREEN 05 — AUDIO

Display:

Tracks

Demos

Sessions

Performances

Reels

Audio samples

Each audio item may contain:

Artwork

Title

Role

Project

Duration

Play

Save

Share

The audio player must follow the existing MusicLinkd audio design system.

Do not create a Spotify clone.

--------------------------------------------------
14. AUDIO PLAYER
--------------------------------------------------

Use the existing MusicLinkd audio player.

Support:

Play / pause

Progress

Waveform

Duration

Track title

Artwork

Queue where already supported

Keep the player compact.

Profile browsing should remain the primary experience.

--------------------------------------------------
15. VIDEO PORTFOLIO
--------------------------------------------------

SCREEN 06 — VIDEO

Show:

Performance videos

Music videos

Studio sessions

Live performances

Behind-the-scenes

Short clips

Use strong thumbnails.

Do not create a social-media feed.

--------------------------------------------------
16. IMAGE GALLERY
--------------------------------------------------

SCREEN 07 — GALLERY

Show:

Performance photography

Studio photography

Events

Equipment

Behind-the-scenes

Editorial grid/masonry where supported by Gestalt.

Use the existing gallery component.

--------------------------------------------------
17. PROJECTS
--------------------------------------------------

SCREEN 08 — PROJECTS

Show professional work.

Each project card:

Project image

Project name

Role

Collaborators

Genre

Year

Status

View Project

Examples:

Album

Single

Film Score

Advertisement

Live Show

Podcast

Production

Do not duplicate the separate Projects system.

The profile should surface projects that already belong to the person's project graph.

--------------------------------------------------
18. DISCOGRAPHY
--------------------------------------------------

Where relevant:

Albums

Singles

EPs

Scores

Credits

Production work

Use role-specific presentation.

A producer's discography should not look identical to a singer's performance portfolio.

--------------------------------------------------
19. SERVICES
--------------------------------------------------

SCREEN 09 — SERVICES

Professionals may offer:

Mixing

Mastering

Production

Session Work

Songwriting

Composition

Voice-over

Teaching

Live Sound

DJ Services

Other supported services

Service card:

Service name

Description

Starting price where available

Availability

Turnaround

Remote / location

CTA:

Enquire

Do not create fake pricing where none exists.

--------------------------------------------------
20. INSTRUMENTS
--------------------------------------------------

SCREEN 10 — INSTRUMENTS

Show:

Primary instrument

Secondary instruments

Experience

Genres

Relevant portfolio

Examples:

Guitar

Bass

Piano

Tabla

Sitar

Drums

Flute

Keyboard

etc.

Use the existing MusicLinkd instrument taxonomy.

--------------------------------------------------
21. LANGUAGES
--------------------------------------------------

Show supported languages.

Examples:

Hindi

English

Punjabi

Tamil

Telugu

Bengali

Marathi

Gujarati

Kannada

Malayalam

Bhojpuri

Use the repository's canonical language catalogue.

Do not create a second taxonomy.

--------------------------------------------------
22. SOCIAL / EXTERNAL MUSIC LINKS
--------------------------------------------------

Where supported:

Spotify

Apple Music

YouTube Music

SoundCloud

Tidal

Audiomack

Bandcamp

Instagram

YouTube

Display these as professional external links.

Do not make them visually dominate the MusicLinkd profile.

--------------------------------------------------
23. CONNECTIONS
--------------------------------------------------

SCREEN 11 — CONNECTIONS

Show:

Mutual connections

Followers

Following

Relevant collaborators

Connection count

Use the existing MusicLinkd relationship model.

Do not turn the profile into a follower-count competition.

--------------------------------------------------
24. COLLABORATORS
--------------------------------------------------

SCREEN 12 — COLLABORATORS

Show relevant people/businesses the person has worked with.

Examples:

Artists

Producers

Engineers

Studios

Labels

Venues

Projects

Each collaborator can open their MusicLinkd profile.

This creates the MusicLinkd professional graph.

--------------------------------------------------
25. OPPORTUNITIES
--------------------------------------------------

SCREEN 13 — OPPORTUNITIES

Show relevant professional opportunities.

Examples:

Looking for Guitarist

Looking for Vocalist

Looking for Producer

Available for Session Work

Available for Shows

Looking for Studio

Use the existing Opportunities system.

Do not create a duplicate opportunity engine.

--------------------------------------------------
26. MARKETPLACE CONNECTION
--------------------------------------------------

If the person sells or rents equipment:

Show:

Marketplace listings

Rental listings

Services

Do not duplicate Marketplace functionality.

The profile should provide entry points into the existing Marketplace.

--------------------------------------------------
27. STUDIO CONNECTION
--------------------------------------------------

If the person owns/manages a studio:

Show:

Studio

Services

Equipment

Availability

Booking

This should link to the existing Studio/Business profile rather than creating duplicate information.

--------------------------------------------------
28. REVIEWS
--------------------------------------------------

SCREEN 14 — REVIEWS

Show:

Overall rating where supported

Reviews

Reviewer identity

Context

Project / service / booking context

Response

Keep reviews professional.

Do not make this look like an ecommerce review wall.

--------------------------------------------------
29. EXPERIENCE / CREDITS
--------------------------------------------------

SCREEN 15 — EXPERIENCE

Where relevant:

Years active

Projects

Artists worked with

Studios

Labels

Films

Brands

Events

Roles

Use structured information rather than a generic CV.

--------------------------------------------------
30. FEATURED WORK
--------------------------------------------------

Allow the profile owner to designate:

Featured Track

Featured Project

Featured Video

Featured Service

Featured Achievement

Only one or a small number should be visually dominant.

The profile should not become a wall of equal-weight cards.

--------------------------------------------------
31. QR / DIGITAL IDENTITY
--------------------------------------------------

SCREEN 16 — SHARE PROFILE

MusicLinkd provides professional digital identity.

Support:

QR code

Vanity URL

Share Card

Copy Link

Share Profile

Save Contact

The share experience should feel like a professional digital business card.

--------------------------------------------------
32. SHARE CARD
--------------------------------------------------

Create a beautiful profile share preview containing:

Profile image

Name

Role

Location

Verification

MusicLinkd URL

QR code where appropriate

Use the existing brand system.

--------------------------------------------------
33. PROFILE ACTIONS
--------------------------------------------------

Contextual primary actions:

Connect

Message

Follow

Hire

Invite to Project

Book

Enquire

View Portfolio

The actions should adapt to the profile type and viewer context.

Example:

Musician

→ Connect
→ Message
→ Invite to Project

Producer

→ Message
→ Hire
→ View Portfolio

Studio owner

→ Book
→ Message
→ View Studio

Do not show every action simultaneously.

--------------------------------------------------
34. PROFILE OWNER MODE
--------------------------------------------------

SCREEN 17 — MY PROFILE

When the owner views their own profile:

Replace public actions with:

Edit Profile

Share

View as Public

Analytics

Manage Portfolio

Manage Services

Manage Availability

Settings

Do not create a completely different visual profile.

It is the same profile with owner controls.

--------------------------------------------------
35. EDIT PROFILE
--------------------------------------------------

SCREEN 18 — EDIT PROFILE

Organise editing into sections:

Identity

Professional

Music

Portfolio

Services

Availability

Social Links

Privacy

Verification

Do not create one giant form.

Use progressive disclosure.

--------------------------------------------------
36. PROFILE COMPLETION
--------------------------------------------------

SCREEN 19 — PROFILE COMPLETION

Show useful completion guidance.

Example:

Your profile is 78% complete.

Missing:

Professional bio

Primary instrument

Portfolio

Availability

Profile image

CTA:

Complete Profile

Do not gamify excessively.

--------------------------------------------------
37. PROFILE ANALYTICS
--------------------------------------------------

SCREEN 20 — PROFILE INSIGHTS

Show:

Profile views

Portfolio views

Audio plays

Video plays

Search appearances

Profile shares

Connection requests

Messages

Geographic audience where supported

Keep this lightweight and consistent with the existing Analytics module.

--------------------------------------------------
38. PROFILE STATES
--------------------------------------------------

Design:

Complete profile

Incomplete profile

Unverified

Verified

Private

Unavailable

No portfolio

No audio

No projects

No services

No connections

No reviews

Do not use generic empty-state illustrations when the existing Gestalt system provides another pattern.

--------------------------------------------------
39. BLOCK / REPORT
--------------------------------------------------

Design contextual actions:

Block

Report

Restrict where supported

Report profile

Report content

Use the existing MusicLinkd safety/moderation patterns.

These actions belong in More / contextual menus.

--------------------------------------------------
40. PROFILE PRIVACY
--------------------------------------------------

Where supported, allow:

Public

Connections only

Private

Control visibility of:

Contact information

Portfolio

Activity

External links

Availability

Location

Use the existing privacy architecture.

--------------------------------------------------
41. PROFILE NAVIGATION
--------------------------------------------------

Use a clear profile information architecture.

Recommended top-level sections:

Overview

Portfolio

Projects

Services

Activity

About

Do not create ten visible tabs.

Use progressive navigation.

A strong profile should be scannable immediately.

--------------------------------------------------
42. PROFILE HOME STRUCTURE
--------------------------------------------------

Recommended order:

Hero

↓

Primary identity

↓

Actions

↓

Professional summary

↓

Featured work

↓

Music / instruments / genres

↓

Portfolio

↓

Projects

↓

Services

↓

Experience / credits

↓

Collaborators

↓

Reviews

↓

Connections

↓

Related MusicLinkd activity

The exact ordering should adapt to the person's role.

--------------------------------------------------
43. PROFILE PERSONALISATION
--------------------------------------------------

Different roles should naturally surface different information.

Singer:

Hero

Vocal identity

Featured performances

Audio

Video

Discography

Languages

Genres

Services

Projects

Musician:

Hero

Instrument

Genres

Performance

Audio

Projects

Session work

Producer:

Hero

Production identity

Featured work

Credits

Discography

Projects

Services

Studio:

Do not use this person-profile system for business profiles.

Link to the Business Profile experience instead.

--------------------------------------------------
44. DISCOVERY CONNECTION
--------------------------------------------------

A profile discovered through Search or Discover should open into the same canonical profile.

Do not create separate profile designs for:

Search

Discovery

Messaging

Marketplace

Projects

Each should resolve to the same MusicLinkd identity.

--------------------------------------------------
45. MESSAGING CONNECTION
--------------------------------------------------

From a profile:

Message

→ opens existing MusicLinkd Messaging.

The conversation should know the context:

Viewed Profile

Relevant project

Relevant service

Marketplace listing

Opportunity

Do not create a new messaging interface.

--------------------------------------------------
46. PROJECT CONNECTION
--------------------------------------------------

Invite to Project

→ existing Project / Collaboration flow.

The profile should never duplicate project management.

--------------------------------------------------
47. MARKETPLACE CONNECTION
--------------------------------------------------

Hire

→ Service / Marketplace context

View Listings

→ Marketplace

Book

→ Studio / Booking

Everything should connect to the existing ecosystem.

--------------------------------------------------
48. MOTION
--------------------------------------------------

Use existing MusicLinkd Gestalt motion.

Appropriate interactions:

Profile hero entrance

Tab transitions

Audio play

Save

Connect

Follow

Profile completion

Gallery transition

Expandable biography

Portfolio reveal

Share card

Do not animate everything.

Motion should reinforce hierarchy.

--------------------------------------------------
49. RESPONSIVE AND ANDROID
--------------------------------------------------

This is an Android mobile application.

Design for:

Small Android devices

Standard Android devices

Large Android devices

Use:

Safe areas

Responsive spacing

Touch-friendly controls

Native-feeling scrolling

Sticky contextual actions where useful

Do not design around a single screenshot.

--------------------------------------------------
50. ACCESSIBILITY
--------------------------------------------------

Follow the existing MusicLinkd Accessibility Implementation Checklist.

Ensure:

Touch targets

Contrast

Screen reader labels

Content descriptions

Accessible media controls

Clear focus states

No colour-only communication

--------------------------------------------------
51. COMPONENTS
--------------------------------------------------

Reuse or create canonical components only when necessary.

Profile components:

Profile Hero

Avatar

Verification Badge

Availability Badge

Role Badge

Professional Summary

Stat Row

Profile Action Group

Music Identity Card

Instrument Chip

Genre Chip

Language Chip

Audio Track Card

Audio Player

Video Card

Gallery

Project Card

Service Card

Experience Card

Credit Card

Collaborator Card

Review Card

Social Link

Share Card

QR Card

Profile Completion

Profile Section

Do not create duplicate components that already exist in Gestalt.

--------------------------------------------------
52. EMPTY STATES
--------------------------------------------------

Design useful empty states:

No portfolio

No tracks

No videos

No projects

No services

No reviews

No collaborators

No opportunities

No marketplace listings

No connections

Each should explain the state and provide an appropriate CTA when the viewer is the profile owner.

Public viewers should not see unnecessary management CTAs.

--------------------------------------------------
53. LOADING STATES
--------------------------------------------------

Use existing MusicLinkd skeleton patterns for:

Profile

Portfolio

Audio

Projects

Reviews

Connections

Services

Do not create custom loaders.

--------------------------------------------------
54. ERROR / OFFLINE STATES
--------------------------------------------------

Use the existing MusicLinkd error catalogue and offline architecture.

Previously cached profile information should remain usable where supported.

--------------------------------------------------
55. PROFILE JSON / DATA CONTRACT
--------------------------------------------------

Every profile screen must have an explicit screen identity and data contract.

Example:

FLOW:

FLOW-02

SCREEN:

PROFILE-01

SCREEN NAME:

Person Profile

MODULE:

Profile

ROUTE:

/profile/:username

Embedded JSON contract should conceptually support:

{
  "flow": "FLOW-02",
  "screen": "PROFILE-01",
  "screenName": "Person Profile",
  "module": "Profile",
  "dataSource": "users",
  "entities": [
    "user",
    "professionalProfile",
    "portfolio",
    "projects",
    "services",
    "connections",
    "reviews"
  ],
  "states": [
    "loading",
    "loaded",
    "private",
    "error",
    "offline"
  ],
  "actions": [
    "connect",
    "follow",
    "message",
    "share",
    "hire",
    "inviteToProject"
  ]
}

This is a DATA CONTRACT.

Do not treat it as hardcoded production data.

Use the repository's actual schema terminology wherever already defined.

--------------------------------------------------
56. SCHEMA EVOLUTION
--------------------------------------------------

The existing schema is the starting point, not an immutable limitation.

If the UI exposes a genuine missing data requirement:

DO NOT invent a fake field.

Identify:

Existing field

Missing requirement

Proposed field

Reason

Affected screen

Then update the canonical schema/documentation before treating the new field as part of the product model.

--------------------------------------------------
57. GESTALT EVOLUTION
--------------------------------------------------

The same rule applies to the design system.

If Profile requires a genuinely reusable pattern:

Do not create a screen-specific component.

Determine whether it belongs in Gestalt.

If yes:

Add the component/token/pattern to Gestalt.

Then use it on the Profile.

This ensures Profile improves the overall MusicLinkd system rather than becoming an isolated design.

--------------------------------------------------
58. PROFILE DESIGN PRINCIPLE
--------------------------------------------------

The profile is not a page about a person.

It is the person's:

PROFESSIONAL MUSIC IDENTITY.

It should allow someone to understand:

WHO

WHAT

WHERE

HOW GOOD

WHAT THEY HAVE DONE

WHAT THEY OFFER

WHO THEY WORK WITH

AND

HOW TO WORK WITH THEM

within seconds.

--------------------------------------------------
59. FINAL DELIVERABLE
--------------------------------------------------

Design the complete high-fidelity MusicLinkd Person Profile experience.

Include:

Public Profile

Profile Overview

About

Professional Details

Music Identity

Instruments

Genres

Languages

Audio Portfolio

Video Portfolio

Gallery

Projects

Discography

Services

Experience

Credits

Collaborators

Reviews

Opportunities

Marketplace connection

Profile Sharing

QR Identity

Owner Profile

Edit Profile

Profile Completion

Profile Analytics

Privacy

Report / Block

Loading

Empty

Error

Offline

All required bottom sheets, dialogs, menus and contextual states.

Use the existing MusicLinkd Gestalt Design System throughout.

Do not create wireframes.

Do not create a generic social profile.

Do not create a LinkedIn clone.

Do not create an Instagram clone.

Create one cohesive, premium, professional MusicLinkd identity experience.

The final profile should feel like:

A professional portfolio
+
a music identity
+
a collaboration profile
+
a network identity

inside one MusicLinkd ecosystem.


   7. 
   8. 
   9.


---

## Source 2 — session `a703efb8`, 2026-08-29T08:49:03 (72,787 chars)

flow 7 you fecth and refetch from the https://preview--talent-chord.lovable.app/marketplace loaavable link


i will also share the long prompt

FLOW — MARKET
Purpose: Buy, Sell & Rent within the MusicLinkd ecosystem.
Market is one unified module, but the user journeys split into Buy, Sell, and Rent.


FLOW 1 — BUY
Market
 ↓
Browse / Search
 ↓
Category
 ↓
Filters
 ↓
Listing Results
 ↓
Listing Detail
 ↓
Seller / Provider Profile
 ↓
Message / Enquire
 ↓
Make Offer
 ↓
Negotiate
 ↓
Accept Offer
 ↓
Purchase / Transaction
 ↓
Order / Transaction Status
 ↓
Completed
 ↓
Review
Buy screens

1. Market Home
2. Search
3. Category
4. Search Results
5. Filters
6. Listing Detail
7. Seller Profile
8. Make Offer
9. Marketplace Conversation
10. Offer Status
11. Purchase / Transaction
12. Transaction Detail
13. Completed Purchase
14. Review



FLOW 2 — SELL
Market
 ↓
Sell / List Item
 ↓
Choose Listing Type
 ↓
Add Photos / Media
 ↓
Add Product Details
 ↓
Set Condition
 ↓
Set Price
 ↓
Location
 ↓
Review Listing
 ↓
Publish
 ↓
Listing Live
 ↓
Receive Enquiry / Offer
 ↓
Message / Negotiate
 ↓
Accept
 ↓
Transaction
 ↓
Mark Sold
 ↓
Completed
Sell screens

1. Market Home
2. Sell / Create Listing
3. Choose Listing Type
4. Add Media
5. Item Details
6. Condition
7. Pricing
8. Location
9. Listing Preview
10. Publish Confirmation
11. My Listings
12. Listing Detail — Owner
13. Enquiries
14. Offers
15. Marketplace Conversation
16. Transaction
17. Mark as Sold
18. Completed Sale



FLOW 3 — RENT
Market
 ↓
Rent
 ↓
Browse / Search
 ↓
Category
 ↓
Filters
 ↓
Rental Listing
 ↓
Equipment Detail
 ↓
Check Availability
 ↓
Select Dates
 ↓
Rental Summary
 ↓
Request Rental
 ↓
Message / Negotiate
 ↓
Provider Accepts
 ↓
Rental Confirmed
 ↓
Pickup / Delivery
 ↓
Active Rental
 ↓
Return
 ↓
Completed
 ↓
Review
Rent screens

1. Rental Home
2. Search
3. Category
4. Rental Results
5. Filters
6. Rental Detail
7. Provider Profile
8. Availability
9. Rental Summary
10. Rental Request
11. Rental Conversation
12. Offer / Negotiation
13. Rental Confirmation
14. My Rentals
15. Active Rental
16. Extend Rental
17. Return Equipment
18. Rental Completed
19. Review



FLOW 4 — RENT OUT
This is the seller-side rental journey.
Market
 ↓
Rent Out Equipment
 ↓
Choose Equipment
 ↓
Add Media
 ↓
Equipment Details
 ↓
Condition
 ↓
Rental Pricing
 ↓
Availability
 ↓
Pickup / Delivery
 ↓
Terms
 ↓
Preview
 ↓
Publish
 ↓
Rental Listing Live
 ↓
Receive Rental Request
 ↓
Review Request
 ↓
Message / Negotiate
 ↓
Accept
 ↓
Rental Confirmed
 ↓
Equipment Handed Over
 ↓
Active Rental
 ↓
Return
 ↓
Complete Rental
Rent-out screens

1. Rent Out
2. Choose Equipment
3. Add Media
4. Equipment Details
5. Condition
6. Rental Pricing
7. Availability Calendar
8. Pickup / Delivery
9. Rental Terms
10. Listing Preview
11. Publish
12. My Rental Listings
13. Rental Request
14. Rental Conversation
15. Accept / Decline / Counter
16. Rental Confirmed
17. Active Rental
18. Return
19. Completed Rental



MARKET — SHARED SCREENS
These should be shared components/experiences, not duplicated for Buy/Sell/Rent:
Market Home
Search
Categories
Filters
Sort
Listing Card
Listing Detail
Seller / Provider Profile
Save
Share
Messaging
Offers
Reviews
Notifications
Report
Block
Location
Empty States
Loading States
Error States
Market top-level structure
MARKET
│
├── Buy
│   ├── Instruments
│   ├── Equipment
│   ├── Products
│   └── Services
│
├── Rent
│   ├── Instruments
│   ├── Audio Equipment
│   ├── Studio Equipment
│   ├── DJ Equipment
│   └── Live Equipment
│
├── Sell
│   └── Create Listing
│
├── Rent Out
│   └── Create Rental
│
├── Saved
├── Nearby
├── My Listings
├── My Purchases
└── My Rentals
Important: Buy, Sell and Rent should feel like one Market ecosystem, not three separate products. The same listing → identity → messaging → transaction architecture should connect all three.



MUSICLINKD — RENTAL EQUIPMENT MARKETPLACE
FULL UI / UX DESIGN PROMPT

Design the complete high-fidelity Android mobile experience for the MusicLinkd Equipment Rental Marketplace.

This is a DESIGN-ONLY task.

Do not implement backend logic, APIs, Firebase, payment infrastructure or database schemas.

Use the existing MusicLinkd Gestalt Design System as the single source of truth.

Do not create a separate rental design system.

--------------------------------------------------
1. PRODUCT PURPOSE
--------------------------------------------------

MusicLinkd is India's professional network and marketplace for the music industry.

The Rental Marketplace allows musicians, artists, studios, producers, event professionals and businesses to discover, rent and list music equipment.

The experience should support:

DISCOVER
→ equipment

EVALUATE
→ condition
→ specifications
→ pricing
→ availability
→ location
→ owner/provider
→ trust

REQUEST
→ rental dates
→ quantity
→ delivery/pickup
→ enquiry

NEGOTIATE
→ message
→ clarify requirements
→ discuss terms

BOOK
→ confirm rental
→ deposit
→ rental period
→ booking status

RETURN
→ return equipment
→ complete rental
→ review provider

This is NOT a generic ecommerce rental UI.

It is a professional music-equipment rental marketplace.

--------------------------------------------------
2. UX BENCHMARK
--------------------------------------------------

Use Facebook Marketplace as a reference for:

• Discovery
• Search
• Location
• Listing cards
• Filters
• Seller identity
• Saved listings
• Marketplace navigation

Use Airbnb as a reference for:

• Availability
• Date selection
• Pricing breakdown
• Host/provider trust
• Booking flow
• Reviews
• Clear transaction states

Use modern music-product experiences as references for:

• Equipment presentation
• Specifications
• Audio/visual media
• Professional terminology

Do NOT copy Facebook Marketplace or Airbnb visually.

The final experience must feel distinctly MusicLinkd.

--------------------------------------------------
3. MUSICLINKD DESIGN SYSTEM
--------------------------------------------------

Use the existing MusicLinkd Gestalt Design System.

Reuse existing:

• Typography
• Colour tokens
• Spacing
• Radius
• Elevation
• Buttons
• Chips
• Cards
• Bottom sheets
• Navigation
• Forms
• Icons
• Avatars
• Badges
• Loading states
• Empty states
• Motion

Do not introduce arbitrary new colours or typography.

Do not use generic Material 3 styling where it conflicts with MusicLinkd.

Do not use old hardcoded colour values from previous concepts.

The current MusicLinkd Gestalt tokens always win.

--------------------------------------------------
4. VISUAL DIRECTION
--------------------------------------------------

Premium.

Editorial.

Music-first.

Photography-led.

Professional.

Airy.

Confident.

Easy to scan.

The equipment should be the visual hero.

Use strong photography for:

• Microphones
• Guitars
• Keyboards
• Drum kits
• Mixers
• Audio interfaces
• Speakers
• Monitors
• DJ equipment
• Amplifiers
• PA systems
• Studio equipment

Avoid:

• Generic ecommerce templates
• Dense product grids
• Excessive badges
• Cheap marketplace aesthetics
• Excessive gradients
• Overly colourful cards
• Fake 3D renders
• Generic stock imagery
• Excessive shadows

--------------------------------------------------
5. RENTAL CATEGORIES
--------------------------------------------------

Support the existing MusicLinkd marketplace taxonomy.

Primary categories:

INSTRUMENTS

• Guitar
• Bass
• Keyboard
• Piano
• Drums
• Tabla
• Harmonium
• Sitar
• Sarod
• Santoor
• Bansuri
• Veena
• Tanpura
• Dholak
• Other instruments

AUDIO EQUIPMENT

• Microphones
• Audio Interfaces
• Mixers
• Headphones
• Studio Monitors
• Speakers
• PA Systems
• Amplifiers
• Cables
• DI Boxes

STUDIO EQUIPMENT

• Recording Equipment
• Monitoring
• Microphones
• Controllers
• Outboard Gear
• Backline

DJ EQUIPMENT

• DJ Controllers
• Turntables
• Mixers
• Speakers
• Lighting
• Performance Equipment

LIVE / EVENT EQUIPMENT

• PA Systems
• Speakers
• Microphones
• Mixers
• Amplifiers
• Stage Equipment

Do not create conflicting categories if the repository already contains an approved taxonomy.

--------------------------------------------------
6. RENTAL HOME
--------------------------------------------------

SCREEN 01 — RENTAL MARKETPLACE

Header:

Rent Equipment

Subtitle:

Professional music equipment when you need it.

Search:

"Search microphones, guitars, mixers..."

Location:

Mumbai, Maharashtra

Then category discovery.

CATEGORY TILES:

Instruments

Audio

Studio

DJ

Live

Other

Then:

RECOMMENDED FOR YOU

Based on:

• instruments
• genres
• previous activity
• location
• saved items

Then:

NEAR YOU

Show equipment available nearby.

Then:

POPULAR RENTALS

Show highly rented equipment.

Then:

RECENTLY VIEWED

Then:

SAVED RENTALS

The landing page should immediately feel useful rather than like a catalogue.

--------------------------------------------------
7. SEARCH
--------------------------------------------------

SCREEN 02 — RENTAL SEARCH

Search should support:

Equipment name

Brand

Model

Category

Location

Provider

Example:

"Shure SM7B"

Show:

Recent Searches

Popular Equipment

Suggested Categories

Available Nearby

Search Results

Search should prioritise available equipment.

--------------------------------------------------
8. SEARCH RESULTS
--------------------------------------------------

SCREEN 03 — RENTAL RESULTS

Header:

Microphones

Search

Location

Result count

Sort

Filter

Listing cards.

Each result should clearly communicate:

Equipment

Availability

Rental price

Location

Provider

Condition

Save

Example:

Shure SM7B

₹900 / day

Available Aug 18–22

Andheri, Mumbai

Excellent condition

by SoundHouse Studio

--------------------------------------------------
9. FILTERS
--------------------------------------------------

SCREEN 04 — FILTER BOTTOM SHEET

Filters:

Location

Distance

Category

Rental dates

Price per day

Price per week

Condition

Brand

Availability

Delivery available

Pickup available

Verified provider

Security deposit

Use progressive disclosure.

Do not create an enormous form.

Bottom actions:

Clear all

Show results

--------------------------------------------------
10. SORT
--------------------------------------------------

SCREEN 05 — SORT

Recommended

Nearest

Lowest rental price

Highest rental price

Most popular

Best rated

Newest

Only show meaningful options.

--------------------------------------------------
11. EQUIPMENT RENTAL CARD
--------------------------------------------------

Create a canonical MusicLinkd Rental Card.

Card contains:

Large equipment image

Save

Equipment name

Brand / model

Condition

Rental price

Rental period

Location

Availability

Provider

Verification

Rating where available

Primary CTA:

Check availability

Cards must be visually clean.

The image should carry significant visual weight.

--------------------------------------------------
12. EQUIPMENT DETAIL
--------------------------------------------------

SCREEN 06 — RENTAL DETAIL

This is the core rental experience.

Large media gallery.

Support:

Images

Video where available

Multiple equipment angles

Then:

Equipment name

Brand

Model

Condition

Rating

Save

Share

Location

Availability

Then:

RENTAL PRICE

₹X / day

₹X / week

₹X / month

Clearly explain pricing.

Then:

SECURITY DEPOSIT

₹X

Then:

DESCRIPTION

Then:

SPECIFICATIONS

Example:

Brand

Model

Type

Connectivity

Included accessories

Power requirements

Weight

Condition

Year

Then:

WHAT'S INCLUDED

Microphone

Shock mount

Cable

Case

etc.

Then:

PROVIDER

Provider image

Name

Business / professional role

Verification

Rating

Response time

Location

View Profile

Message

Then:

REVIEWS

Then:

RELATED EQUIPMENT

--------------------------------------------------
13. AVAILABILITY
--------------------------------------------------

SCREEN 07 — CHECK AVAILABILITY

Use a clean calendar interface.

User selects:

Start date

End date

Quantity

Then show:

Available

Unavailable

Partially available

Use the existing MusicLinkd calendar/date-picker components.

Do not invent a separate calendar visual language.

--------------------------------------------------
14. RENTAL PRICE CALCULATION
--------------------------------------------------

SCREEN 08 — RENTAL SUMMARY

Show:

Equipment

Rental period

Daily rate

Number of days

Subtotal

Security deposit

Delivery fee where applicable

Taxes/fees where applicable

Estimated total

Make the pricing hierarchy extremely clear.

Do not hide the security deposit.

If payment is not implemented, clearly treat the amount as an estimate rather than a completed transaction.

CTA:

Request Rental

--------------------------------------------------
15. RENTAL REQUEST
--------------------------------------------------

SCREEN 09 — RENTAL REQUEST

Equipment summary

Rental dates

Quantity

Pickup / delivery

Location

Notes

Optional message to provider

CTA:

Send Rental Request

Secondary:

Save Draft

Do not imply payment has occurred.

--------------------------------------------------
16. DELIVERY / PICKUP
--------------------------------------------------

SCREEN 10 — DELIVERY OPTIONS

Options:

Pickup

Provider delivery

Courier / delivery where supported

Show:

Location

Estimated fee

Estimated timing

Instructions

Where delivery is unavailable:

Pickup only

Do not create fake delivery availability.

--------------------------------------------------
17. RENTAL NEGOTIATION
--------------------------------------------------

SCREEN 11 — RENTAL CONVERSATION

This must connect to the existing MusicLinkd Messaging & Collaboration experience.

Do NOT create a second messaging system.

The conversation should contain a contextual Rental Card:

Equipment

Rental dates

Price

Condition

Location

Provider

Request status

Then contextual actions:

Confirm

Modify dates

Ask question

Cancel request

The existing MusicLinkd Messaging design system governs the conversation itself.

--------------------------------------------------
18. RENTAL OFFER
--------------------------------------------------

SCREEN 12 — RENTAL OFFER

Allow the provider/buyer flow to surface:

Equipment

Requested dates

Original price

Offered price

Deposit

Notes

Offer status

Actions:

Accept

Decline

Counter Offer

All offer communication should connect to Messaging.

--------------------------------------------------
19. RENTAL CONFIRMATION
--------------------------------------------------

SCREEN 13 — RENTAL CONFIRMED

Show:

Confirmation state

Equipment

Provider

Rental dates

Pickup/delivery

Price

Deposit

Booking reference

Instructions

Primary:

View Rental

Secondary:

Message Provider

The visual treatment should be calm and trustworthy.

--------------------------------------------------
20. MY RENTALS
--------------------------------------------------

SCREEN 14 — MY RENTALS

Sections:

Upcoming

Active

Completed

Cancelled

Each rental card:

Equipment

Provider

Dates

Status

Price

Quick action

Examples:

View

Message

Extend

Return

Review

--------------------------------------------------
21. ACTIVE RENTAL
--------------------------------------------------

SCREEN 15 — ACTIVE RENTAL

Show:

Equipment

Rental period

Time remaining

Provider

Pickup/delivery information

Return deadline

Deposit

Rental terms

Actions:

Message Provider

Request Extension

Report Issue

Return Equipment

--------------------------------------------------
22. EXTEND RENTAL
--------------------------------------------------

SCREEN 16 — EXTEND RENTAL

Show current rental.

Current end date.

New end date.

Additional rental cost.

Availability status.

CTA:

Request Extension

If extension is unavailable:

Clearly explain why.

--------------------------------------------------
23. RETURN EQUIPMENT
--------------------------------------------------

SCREEN 17 — RETURN

Show:

Equipment

Return date

Return method

Condition confirmation

Optional photos

Notes

CTA:

Confirm Return

Do not imply that a deposit has been refunded unless the actual product supports that state.

--------------------------------------------------
24. RENTAL COMPLETION
--------------------------------------------------

SCREEN 18 — COMPLETED RENTAL

Show:

Rental completed

Equipment

Provider

Rental dates

Final status

Then:

Rate Provider

Rate Equipment

Write Review

Book Again

Browse Similar Equipment

--------------------------------------------------
25. REVIEWS
--------------------------------------------------

SCREEN 19 — RENTAL REVIEW

Rate:

Equipment

Provider

Experience

Optional written review

Optional tags:

Equipment condition

Accuracy

Communication

Pickup

Delivery

Value

Keep the review interface lightweight.

--------------------------------------------------
26. PROVIDER PROFILE
--------------------------------------------------

SCREEN 20 — RENTAL PROVIDER

The provider should use the existing MusicLinkd professional/business identity.

Show:

Profile photo/logo

Name

Role

Business

Verification

Location

Rating

Response rate

Response time

Years active

Active listings

Reviews

Portfolio

Other equipment

Primary actions:

Message

View Listings

Connect / Follow where appropriate

Do not create a separate rental-only profile.

--------------------------------------------------
27. PROVIDER LISTINGS
--------------------------------------------------

SCREEN 21 — PROVIDER EQUIPMENT

Show all available equipment from the provider.

Categories

Availability

Rental prices

Condition

Location

Save

This encourages users to rent multiple items from one provider.

--------------------------------------------------
28. SAVED RENTALS
--------------------------------------------------

SCREEN 22 — SAVED

Sections:

Saved Equipment

Saved Providers

Saved Searches

Saved Studios / Spaces

Each saved item should retain useful availability context.

Empty state:

"No saved rentals yet."

CTA:

Explore Equipment

--------------------------------------------------
29. RECENTLY VIEWED
--------------------------------------------------

SCREEN 23 — RECENTLY VIEWED

Show:

Equipment

Provider

Price

Availability

Save

Remove

This should help users return to unfinished decisions.

--------------------------------------------------
30. NEARBY RENTALS
--------------------------------------------------

SCREEN 24 — NEARBY

Show:

Equipment near me

Studios near me

Rental providers near me

Use:

Distance

Location

Availability

The map is optional.

Do not force map-first navigation.

The list must remain useful on its own.

--------------------------------------------------
31. SELL / RENT OUT EQUIPMENT
--------------------------------------------------

SCREEN 25 — LIST EQUIPMENT FOR RENT

Entry:

"Rent out equipment"

Ask:

What are you renting?

Instrument

Audio Equipment

Studio Equipment

DJ Equipment

Live Equipment

Other

Then continue into the listing flow.

--------------------------------------------------
32. CREATE RENTAL LISTING — MEDIA
--------------------------------------------------

SCREEN 26

Add photos.

Set cover image.

Reorder.

Add video.

Use real equipment imagery.

Provide guidance:

"Show the actual equipment you are renting."

Avoid generic placeholders.

--------------------------------------------------
33. CREATE RENTAL LISTING — INFORMATION
--------------------------------------------------

SCREEN 27

Equipment name

Brand

Model

Category

Description

Condition

Included accessories

Location

Availability

Rental pricing

Daily

Weekly

Monthly

Security deposit

Pickup

Delivery

Do not display irrelevant fields.

The form should adapt to the equipment type.

--------------------------------------------------
34. CREATE RENTAL LISTING — AVAILABILITY
--------------------------------------------------

SCREEN 28

Calendar.

Mark unavailable dates.

Set recurring availability where appropriate.

Set:

Minimum rental duration

Maximum rental duration

Notice period

Do not make availability management unnecessarily complex.

--------------------------------------------------
35. CREATE RENTAL LISTING — REVIEW
--------------------------------------------------

SCREEN 29

Show exactly how the listing will appear to renters.

Preview:

Image

Equipment

Condition

Price

Availability

Provider

Location

Terms

CTA:

Publish Rental

Save Draft

Edit

--------------------------------------------------
36. MY RENTAL LISTINGS
--------------------------------------------------

SCREEN 30

Sections:

Drafts

Pending Review

Active

Reserved

Currently Rented

Unavailable

Completed

Archived

Each card shows:

Equipment

Availability

Rental price

Views

Saves

Enquiries

Status

--------------------------------------------------
37. PROVIDER RENTAL INSIGHTS
--------------------------------------------------

SCREEN 31

Lightweight marketplace insights.

Show:

Listing views

Saves

Enquiries

Rental requests

Bookings

Revenue where supported

Response rate

Top equipment

Do not turn this into an enterprise analytics dashboard.

--------------------------------------------------
38. TRUST & VERIFICATION
--------------------------------------------------

Use MusicLinkd's existing verification system.

Potential signals:

Verified Identity

Verified Business

Professional

Industry Verified

MusicLinkd Member

Rental Provider

Show only the most meaningful signals.

Also support:

Rating

Completed Rentals

Response Rate

Response Time

These are more useful than decorative badges.

--------------------------------------------------
39. REPORTING / SAFETY
--------------------------------------------------

Design user-facing states for:

Report Equipment

Report Provider

Report Scam

Report Incorrect Information

Block Provider

Do not make these actions visually dominant.

Use the existing MusicLinkd moderation patterns.

--------------------------------------------------
40. NOTIFICATIONS
--------------------------------------------------

Design notification states for:

Rental request received

Rental request accepted

Rental request declined

Counter offer

Provider message

Availability update

Booking confirmation

Rental starting soon

Return reminder

Extension request

Return confirmed

Review request

Use the existing MusicLinkd notification architecture and visual language.

--------------------------------------------------
41. EMPTY STATES
--------------------------------------------------

Design:

No rental results

No nearby equipment

No saved rentals

No rental history

No active rentals

No listings

No availability

No enquiries

No offers

No reviews

Each state should provide a useful next action.

--------------------------------------------------
42. LOADING STATES
--------------------------------------------------

Use existing MusicLinkd skeleton patterns.

Design loading states for:

Rental home

Search results

Listing detail

Calendar

Provider profile

Reviews

My rentals

Rental requests

--------------------------------------------------
43. ERROR / OFFLINE
--------------------------------------------------

Use the existing MusicLinkd error catalogue and offline states.

Do not create generic marketplace error patterns.

Previously loaded rental information should remain understandable where supported.

--------------------------------------------------
44. BOTTOM NAVIGATION
--------------------------------------------------

Use the existing MusicLinkd global navigation.

Do not redesign the app navigation for rentals.

Marketplace remains under:

MARKET

Rental is a Marketplace sub-experience.

--------------------------------------------------
45. COMPONENTS
--------------------------------------------------

Create/reuse:

Rental Header

Rental Search

Category Tile

Rental Listing Card

Equipment Card

Provider Card

Availability Badge

Condition Badge

Price Block

Location Row

Rating

Save Button

Calendar

Date Range Picker

Filter Sheet

Sort Sheet

Rental Summary

Price Breakdown

Rental Request Card

Offer Card

Booking Card

Provider Summary

Review Card

Rental Status

Empty State

Skeleton

Create Listing Form

Listing Preview

Do not duplicate existing MusicLinkd components.

--------------------------------------------------
46. MOTION
--------------------------------------------------

Use existing MusicLinkd Gestalt motion.

Appropriate motion:

Save interaction

Calendar selection

Filter sheet

Image gallery

Rental request confirmation

Offer transition

Booking confirmation

Return completion

Keep motion subtle and fast.

--------------------------------------------------
47. LOCAL / INDIA-FIRST EXPERIENCE
--------------------------------------------------

MusicLinkd is India-first.

Design for:

₹ pricing

Indian cities

Indian instruments

Regional music equipment

Local providers

Pickup

Local delivery

Security deposits

Indian business profiles

Indian language localisation support

The interface may remain English initially.

The architecture must accommodate localisation.

--------------------------------------------------
48. KEY USER JOURNEYS
--------------------------------------------------

JOURNEY 01 — FIND EQUIPMENT

Market

→ Rent

→ Category

→ Search

→ Filter

→ Listing

→ Availability

→ Request

JOURNEY 02 — RENT

Listing

→ Select dates

→ Price summary

→ Request

→ Message

→ Provider accepts

→ Confirm

JOURNEY 03 — NEGOTIATE

Listing

→ Message

→ Offer

→ Counter Offer

→ Accept

→ Confirm

JOURNEY 04 — ACTIVE RENTAL

My Rentals

→ Active

→ View equipment

→ Message

→ Extend / Return

JOURNEY 05 — RETURN

Active Rental

→ Return

→ Confirm condition

→ Complete

→ Review

JOURNEY 06 — RENT OUT EQUIPMENT

Market

→ Rent Out

→ Choose category

→ Add media

→ Equipment details

→ Pricing

→ Availability

→ Preview

→ Publish

→ Receive request

→ Accept

→ Complete

--------------------------------------------------
49. IMPORTANT PRODUCT PRINCIPLE
--------------------------------------------------

The rental marketplace must NOT become a generic rental application.

Everything should remain connected to MusicLinkd's ecosystem.

Example:

A guitarist searches for a guitar.

MusicLinkd can show:

Available guitars

Nearby rental providers

Relevant studios

Compatible equipment

Musicians using similar equipment

Related services

The user can move naturally between:

Marketplace

Profile

Messaging

Studio

Booking

Portfolio

Connections

This is the MusicLinkd advantage.

--------------------------------------------------
50. DESIGN QUALITY BAR
--------------------------------------------------

Target the UX quality of:

Facebook Marketplace
+
Airbnb
+
Spotify
+
Apple

while remaining distinctly MusicLinkd.

Do not copy their UI.

Use their strengths as interaction references.

The final experience should feel:

Premium

Fast

Trustworthy

Professional

Music-industry-specific

India-first

--------------------------------------------------
51. FINAL DELIVERABLE
--------------------------------------------------

Design the complete high-fidelity Android rental experience.

Include:

Rental Marketplace Home

Search

Categories

Results

Filters

Sort

Rental Listing Card

Rental Detail

Equipment Gallery

Specifications

Provider Profile

Availability

Rental Price Breakdown

Rental Request

Delivery / Pickup

Offer / Negotiation

Messaging handoff

Rental Confirmation

My Rentals

Active Rental

Extension

Return

Completed Rental

Reviews

Saved

Recently Viewed

Nearby

Rent Out Equipment

Create Listing

Availability Management

My Listings

Provider Insights

Notifications

Reporting

Empty States

Loading States

Error States

Bottom Sheets

Dialogs

Contextual states

Use the existing MusicLinkd Gestalt Design System throughout.

Do not create wireframes.

Do not create disconnected screens.

Do not create generic ecommerce rental UI.

Create one cohesive, production-ready MusicLinkd Rental Marketplace experience.

The final design should make it immediately obvious:

MUSICLINKD IS WHERE THE MUSIC INDUSTRY FINDS, RENTS, SHARES AND USES THE EQUIPMENT IT NEEDS.



MUSICLINKD — MARKETPLACE
MASTER UI / UX DESIGN PROMPT

Design the complete high-fidelity Android Marketplace experience for MusicLinkd.

MusicLinkd is India's professional network, discovery platform, collaboration ecosystem and marketplace built specifically for the music industry.

IMPORTANT:
This is a DESIGN TASK ONLY.

Do not implement backend logic.
Do not create Firebase schemas.
Do not create APIs.
Do not modify authentication.
Do not invent a second design system.

The objective is to design the complete Marketplace experience using the EXISTING MusicLinkd Gestalt Design System.

The existing MusicLinkd design system is the visual source of truth.

--------------------------------------------------
1. PRODUCT CONTEXT
--------------------------------------------------

MusicLinkd connects:

• Musicians
• Singers
• Producers
• Composers
• Lyricists
• DJs
• Engineers
• Studios
• Venues
• Music businesses
• Dealers
• Rental providers
• Schools
• Labels
• Event professionals

Marketplace extends this ecosystem into commerce.

It should allow users to:

DISCOVER
→ products
→ instruments
→ equipment
→ services
→ rentals
→ studios
→ opportunities

EVALUATE
→ photos
→ specifications
→ condition
→ price
→ seller
→ location
→ verification
→ reviews
→ MusicLinkd identity

CONNECT
→ message
→ enquire
→ make offer
→ request booking
→ contact seller

TRANSACT
→ buy
→ rent
→ book
→ negotiate
→ complete

The Marketplace is therefore NOT simply an ecommerce catalogue.

It is:

MUSIC DISCOVERY
+
LOCAL MARKETPLACE
+
PROFESSIONAL SERVICES
+
MUSIC INDUSTRY NETWORK

--------------------------------------------------
2. UX BENCHMARK
--------------------------------------------------

Use Facebook Marketplace as a UX benchmark for:

• Marketplace discovery
• Search
• Location-aware browsing
• Category navigation
• Listing cards
• Filters
• Seller identity
• Saved listings
• Listing creation
• Buyer/seller interactions
• Marketplace information hierarchy

However:

DO NOT COPY Facebook Marketplace visually.

DO NOT reproduce Facebook UI.

MusicLinkd must remain a distinctly premium MusicLinkd product.

Also draw interaction principles from:

• Airbnb
• Apple
• Spotify
• Linear
• Instagram
• Behance
• Dribbble

The result should feel like a modern 2026 music-industry marketplace.

--------------------------------------------------
3. DESIGN SYSTEM
--------------------------------------------------

USE THE EXISTING MUSICLINKD GESTALT DESIGN SYSTEM.

Do not create new:

• colours
• typography systems
• spacing systems
• radii
• shadows
• buttons
• cards
• chips
• navigation patterns
• icon styles

unless the existing design system genuinely has no equivalent.

Map every Marketplace screen to existing Gestalt tokens.

The design system must remain the authority.

Do NOT replace the existing MusicLinkd design language with generic:

• Material 3
• Bootstrap
• Tailwind UI
• Facebook UI
• generic ecommerce templates

The Marketplace should look like it belongs inside the existing MusicLinkd application.

--------------------------------------------------
4. VISUAL DIRECTION
--------------------------------------------------

Premium.

Editorial.

Music-first.

Human.

Airy.

Photography-led.

Confident.

Modern.

High information quality without feeling dense.

Use strong imagery for:

• instruments
• studio equipment
• studios
• services
• sellers
• music environments

Cards should have clear hierarchy rather than excessive decoration.

Avoid:

• generic ecommerce grids
• cheap marketplace aesthetics
• excessive badges
• excessive gradients
• dense dashboards
• giant CTA buttons everywhere
• unnecessary borders
• fake 3D objects
• generic AI illustrations
• social-media clutter

--------------------------------------------------
5. MARKETPLACE INFORMATION ARCHITECTURE
--------------------------------------------------

Primary Marketplace sections:

MARKET

├── For You
├── Instruments
├── Audio Equipment
├── Studio Equipment
├── Rentals
├── Services
├── Studios
├── Rehearsal Spaces
├── Music Businesses
├── Nearby
├── Saved
└── My Listings

The actual navigation should remain simple.

Do not expose the entire taxonomy simultaneously.

Use progressive discovery.

--------------------------------------------------
6. MARKETPLACE HOME
--------------------------------------------------

SCREEN 01 — MARKETPLACE HOME

Purpose:

Immediately communicate:

"Find what you need for your music."

Header:

Market

Search

Profile / relevant existing navigation affordance

Primary search field:

"Search instruments, equipment, services..."

Below search:

Location selector

Example:

Mumbai, Maharashtra

Then:

CATEGORY DISCOVERY

Visual category tiles/cards:

Instruments
Audio Gear
Studio
Rentals
Services
Spaces

Then:

RECOMMENDED FOR YOU

Personalised listings.

Examples:

Guitars
Microphones
Interfaces
Studio services

Then:

NEAR YOU

Location-aware listings.

Then:

TRENDING

Popular music equipment/services.

Then:

RECENTLY VIEWED

Where relevant.

Then:

SAVED ITEMS

Where relevant.

The screen should feel personalised rather than like a static catalogue.

--------------------------------------------------
7. CATEGORY EXPERIENCE
--------------------------------------------------

SCREEN 02 — CATEGORY

Example:

Instruments

Header:

Instruments

Search

Category chips / subcategories:

Guitar
Bass
Keyboard
Piano
Drums
Indian Instruments
Strings
Percussion
Wind
Other

Below:

Result count

Sort

Filter

Listing feed/grid depending on the existing MusicLinkd responsive pattern.

Do not make the category page visually overwhelming.

--------------------------------------------------
8. SEARCH EXPERIENCE
--------------------------------------------------

SCREEN 03 — MARKETPLACE SEARCH

Search should feel like a first-class MusicLinkd capability.

Search:

"condenser microphone"

Show:

Recent searches

Popular searches

Suggested categories

Relevant listings

Search results should understand:

• Product
• Brand
• Instrument
• Service
• Location
• Seller
• Category

Search results should clearly communicate why something is relevant.

--------------------------------------------------
9. FILTER EXPERIENCE
--------------------------------------------------

SCREEN 04 — FILTER BOTTOM SHEET

Use the existing MusicLinkd bottom-sheet pattern.

Filters:

Location

Distance

Category

Price

Condition

Brand

Availability

Verified sellers

Seller type

Listing type

Service type

Rental availability

Where relevant:

New
Like New
Excellent
Good
Fair

Bottom actions:

Clear all

Show results

Do not create an unnecessarily complicated filtering interface.

--------------------------------------------------
10. SORTING
--------------------------------------------------

SCREEN 05 — SORT

Options:

Recommended

Newest

Price: Low to High

Price: High to Low

Nearest

Most Saved

Most Viewed

Only show contextually relevant options.

--------------------------------------------------
11. LISTING CARD
--------------------------------------------------

Create a canonical MusicLinkd Marketplace Listing Card.

The card must support multiple listing types.

PRODUCT CARD

Image

Title

Condition

Price

Location

Seller

Verification

Save

SERVICE CARD

Image / visual

Service name

Provider

Rating

Starting price

Location / remote

Verification

Save

RENTAL CARD

Image

Item

Rental price

Duration

Location

Availability

Provider

Save

STUDIO / SPACE CARD

Photography

Name

Location

Rating

Starting price

Availability

Save

Do not force every listing into the same information structure.

--------------------------------------------------
12. LISTING DETAIL
--------------------------------------------------

SCREEN 06 — LISTING DETAIL

This is one of the most important screens.

Use large media.

Media gallery:

Images

Video where supported

Audio where relevant

Hero image

Then:

Title

Price

Condition

Location

Verification

Save

Share

Seller/provider

Then:

DESCRIPTION

Full description.

SPECIFICATIONS

For example:

Brand
Model
Year
Condition
Instrument type
Connectivity
Included accessories

For services:

Specialisation
Experience
Languages
Delivery time
Location
Remote availability

For rentals:

Rental duration
Availability
Deposit
Pickup/delivery
Terms

Then:

SELLER / PROVIDER

Avatar

Name

Role

Verification

MusicLinkd profile

Rating

Response time

Location

View Profile

Then:

REVIEWS

Then:

RELATED LISTINGS

Then sticky contextual CTA.

--------------------------------------------------
13. BUYER ACTIONS
--------------------------------------------------

The primary action must depend on listing type.

PRODUCT:

Make Offer
Buy / Enquire

SERVICE:

Enquire
Book

RENTAL:

Request Rental
Check Availability

STUDIO:

Check Availability
Book

Avoid generic "Buy Now" everywhere.

--------------------------------------------------
14. SELLER IDENTITY
--------------------------------------------------

MusicLinkd has a major advantage over generic marketplaces:

Every seller can have a professional MusicLinkd identity.

Seller presentation should include:

Profile image

Name

MusicLinkd role

Verified badge

Location

Rating

Response rate

Response time

Years active

Relevant professional information

View Profile

Message

This should feel closer to:

Airbnb Host
+
Music professional profile

than an anonymous ecommerce seller.

--------------------------------------------------
15. MUSICLINKD TRUST SYSTEM
--------------------------------------------------

Design trust carefully.

Possible trust signals:

Verified Identity

Verified Business

Professional Profile

Industry Verified

MusicLinkd Member

Seller Rating

Response Rate

Completed Transactions

Years on MusicLinkd

Do not overload cards with badges.

Prioritise the most meaningful trust signal.

--------------------------------------------------
16. SELLER PROFILE
--------------------------------------------------

SCREEN 07 — SELLER / PROVIDER PROFILE

A seller profile is an extension of the MusicLinkd professional identity.

Show:

Profile

Name

Role

Verification

Location

Bio

Professional information

Listings

Services

Reviews

Portfolio

MusicLinkd activity where appropriate

Primary actions:

Message

View Listings

Follow / Connect

Do not create a completely separate marketplace-only identity.

--------------------------------------------------
17. MAKE AN OFFER
--------------------------------------------------

SCREEN 08 — MAKE OFFER

Bottom sheet or focused screen.

Show listing summary.

Current price.

Offer amount.

Optional message.

Offer expiry where supported.

CTA:

Send Offer

After sending:

Offer status:

Pending

Accepted

Declined

Expired

The experience should connect naturally to Messaging.

--------------------------------------------------
18. MARKETPLACE MESSAGING
--------------------------------------------------

Marketplace must integrate with the existing MusicLinkd Messaging & Collaboration flow.

Do NOT create a second messaging system.

When a buyer taps:

Message Seller

the existing Messaging experience opens with contextual Marketplace information.

Conversation header should show the listing.

The conversation can contain:

Listing Card

Offer Card

Price

Condition

Seller

Actions

Make Offer

Accept

Decline

Mark Sold

This should follow the previously designed MusicLinkd Messaging flow.

--------------------------------------------------
19. RENTAL EXPERIENCE
--------------------------------------------------

SCREEN 09 — RENTAL DETAIL

For rental listings:

Item

Images

Provider

Location

Daily price

Weekly price

Monthly price

Security deposit

Availability

Rental conditions

Pickup / delivery

Reviews

CTA:

Check Availability

Then:

SCREEN 10 — RENTAL REQUEST

Calendar

Duration

Dates

Quantity

Deposit

Total estimate

Notes

Request Rental

Do not imply payment if payment infrastructure is not implemented.

--------------------------------------------------
20. SERVICES MARKETPLACE
--------------------------------------------------

SCREEN 11 — SERVICES

MusicLinkd should support professional services such as:

Mixing

Mastering

Music Production

Recording

Session Musicians

Songwriting

Composition

Sound Design

Live Sound

Voice-over

DJ Services

Teaching

Showcase services using:

Provider

Portfolio

Specialisation

Rating

Starting price

Delivery time

Location / remote

CTA:

Enquire

--------------------------------------------------
21. STUDIOS & SPACES
--------------------------------------------------

SCREEN 12 — STUDIO / SPACE MARKETPLACE

Support:

Recording Studios

Mixing Studios

Mastering Studios

Podcast Studios

Jam Rooms

Rehearsal Spaces

Live Venues

Show:

Photography

Location

Facilities

Equipment

Capacity

Pricing

Availability

Rating

Reviews

CTA:

View Availability

Book

This must connect with the existing Studio Booking experience.

Do not create a separate booking architecture visually.

--------------------------------------------------
22. EQUIPMENT MARKETPLACE
--------------------------------------------------

SCREEN 13 — EQUIPMENT

Categories:

Microphones

Audio Interfaces

Mixers

Monitors

Headphones

Amplifiers

PA Systems

DJ Equipment

Recording Equipment

Studio Equipment

Use visually strong product photography.

Include:

Brand

Model

Condition

Price

Location

Seller

Save

--------------------------------------------------
23. INSTRUMENT MARKETPLACE
--------------------------------------------------

SCREEN 14 — INSTRUMENTS

Include:

Guitar

Bass

Keyboard

Piano

Drums

Violin

Tabla

Harmonium

Sitar

Sarod

Santoor

Bansuri

Veena

Tanpura

Dholak

and other instruments defined by the product taxonomy.

Make regional Indian instruments first-class citizens.

Do not make the experience feel like a Western-only music store.

--------------------------------------------------
24. NEARBY MARKETPLACE
--------------------------------------------------

SCREEN 15 — NEARBY

Use location as a major discovery dimension.

Show:

Nearby listings

Nearby studios

Nearby sellers

Nearby rentals

Nearby services

Distance

Location

Map/list relationship if supported by the existing design system.

Do not make a map mandatory.

The list should remain useful without it.

--------------------------------------------------
25. SAVED MARKETPLACE
--------------------------------------------------

SCREEN 16 — SAVED

Sections:

Saved Listings

Saved Searches

Saved Sellers

Saved Services

Saved Studios

Show useful empty states.

Example:

"No saved listings yet."

CTA:

Explore Market

--------------------------------------------------
26. RECENTLY VIEWED
--------------------------------------------------

SCREEN 17 — RECENTLY VIEWED

Show recently viewed listings.

Allow:

Remove

Save

Open listing

This should help users return to unfinished decisions.

--------------------------------------------------
27. MY MARKETPLACE
--------------------------------------------------

SCREEN 18 — MY MARKETPLACE

For users who sell/rent/provide services.

Sections:

My Listings

Drafts

Active

Pending Review

Sold

Rented

Completed

Archived

Enquiries

Offers

Saved

Marketplace Performance

This is a lightweight seller dashboard, NOT a giant analytics dashboard.

--------------------------------------------------
28. CREATE LISTING
--------------------------------------------------

SCREEN 19 — CREATE LISTING

Entry:

"Sell / List"

First ask:

What are you offering?

Product

Instrument

Equipment

Rental

Service

Studio / Space

Other supported type

Then guide the user through the appropriate form.

--------------------------------------------------
29. CREATE LISTING — MEDIA
--------------------------------------------------

SCREEN 20

Add photos.

Drag/reorder.

Set cover image.

Add video where supported.

For audio-related listings:

Audio preview where appropriate.

Use the existing MusicLinkd media components.

--------------------------------------------------
30. CREATE LISTING — INFORMATION
--------------------------------------------------

SCREEN 21

Title

Category

Description

Brand

Model

Condition

Price

Location

Availability

Tags

Only show fields relevant to listing type.

Do not make one giant universal form.

--------------------------------------------------
31. CREATE LISTING — REVIEW
--------------------------------------------------

SCREEN 22

Preview listing exactly as buyers will see it.

Show:

Photos

Title

Price

Description

Seller identity

Location

Condition

CTA:

Publish

Save Draft

Edit

--------------------------------------------------
32. LISTING STATUS
--------------------------------------------------

Design states for:

Draft

Pending Review

Published

Paused

Sold

Rented

Completed

Archived

Rejected where applicable

Use existing MusicLinkd status conventions.

--------------------------------------------------
33. MODERATION / REPORTING
--------------------------------------------------

Design user-facing flows for:

Report Listing

Report Seller

Block Seller

Report Scam

Report Incorrect Information

Do not make moderation visually aggressive.

Use the existing MusicLinkd confirmation and error patterns.

--------------------------------------------------
34. REVIEWS
--------------------------------------------------

SCREEN 23 — REVIEWS

Show:

Overall rating

Rating distribution

Written reviews

Reviewer MusicLinkd identity

Transaction/context where appropriate

Seller response

Allow:

Write Review

Report Review

Reviews should feel trustworthy and professional.

--------------------------------------------------
35. OPPORTUNITIES
--------------------------------------------------

Marketplace should connect naturally with the broader MusicLinkd Opportunities ecosystem.

Examples:

Looking for guitarist

Looking for vocalist

Looking for producer

Looking for studio

Looking for equipment

Do not duplicate the Opportunities module.

Provide contextual entry points where relevant.

--------------------------------------------------
36. SOCIAL / NETWORK CONNECTION
--------------------------------------------------

Marketplace should connect with:

Profiles

Connections

Messaging

Projects

Studios

Bookings

Opportunities

Portfolio

But do not turn Marketplace into a social feed.

Marketplace remains task-oriented.

--------------------------------------------------
37. PERSONALISATION
--------------------------------------------------

Use MusicLinkd's professional graph to make discovery more relevant.

Examples:

Based on your instruments

Based on your genres

Near you

Popular with musicians like you

Because you viewed...

Recommended equipment

Recommended services

Recommended studios

Do not overuse "AI Recommended".

Recommendations should feel useful rather than promotional.

--------------------------------------------------
38. AI-ASSISTED DISCOVERY
--------------------------------------------------

Where appropriate, design optional AI-assisted discovery.

Example:

User:

"I need a vocal microphone under ₹20,000 for home recording."

Result:

Relevant microphones

Compatible interfaces

Nearby sellers

Related accessories

Recommended studios

Relevant MusicLinkd professionals

AI should augment discovery, not dominate the interface.

Do not invent an AI chatbot as the primary Marketplace UI.

--------------------------------------------------
39. MARKETPLACE EMPTY STATES
--------------------------------------------------

Design proper states for:

No listings

No search results

No saved listings

No nearby listings

No offers

No enquiries

No active listings

No reviews

No rental availability

No matching services

Each empty state should explain what happened and provide a useful next action.

--------------------------------------------------
40. LOADING STATES
--------------------------------------------------

Use the existing MusicLinkd skeleton/loading system.

Design loading states for:

Marketplace home

Search

Listing cards

Listing detail

Seller profile

Reviews

Availability

Saved listings

--------------------------------------------------
41. ERROR / OFFLINE STATES
--------------------------------------------------

Respect the existing MusicLinkd error catalogue and offline patterns.

Do not invent generic error UI.

Marketplace should remain useful when previously loaded information is cached.

--------------------------------------------------
42. NOTIFICATIONS
--------------------------------------------------

Design notification states/components for:

New enquiry

New offer

Offer accepted

Offer declined

Seller response

Listing approved

Listing rejected

Listing saved/search updates where supported

Rental request

Booking update

Review received

Do not create a separate notification system.

Use the existing MusicLinkd notification language.

--------------------------------------------------
43. MARKETPLACE ANALYTICS — SELLER
--------------------------------------------------

Design a lightweight seller insights experience.

Show:

Listing views

Saves

Enquiries

Offers

Response rate

Profile visits

Performance by listing

Do not turn this into an enterprise BI dashboard.

--------------------------------------------------
44. BOTTOM NAVIGATION
--------------------------------------------------

Use the existing MusicLinkd five-item navigation:

Home

Discover

Connections

Market

Profile

Messages remain part of the existing messaging architecture according to the current MusicLinkd navigation specification.

Do not redesign the global navigation for Marketplace.

Market must clearly appear as the active destination.

--------------------------------------------------
45. MOTION
--------------------------------------------------

Use the existing MusicLinkd Gestalt motion language.

Marketplace motion should support:

• Image transitions
• Save/favourite feedback
• Filter sheet presentation
• Listing card entrance
• Offer submission
• Booking confirmation
• Publish confirmation
• Gallery interaction
• Pull-to-refresh
• Skeleton transitions

Do not add gratuitous animation.

The product should feel fast.

--------------------------------------------------
46. RESPONSIVE AND DEVICE DESIGN
--------------------------------------------------

This is an Android mobile experience.

Design for:

Small Android devices

Standard Android devices

Large Android devices

Do not design around one screenshot.

Use existing responsive tokens and layout rules.

--------------------------------------------------
47. ACCESSIBILITY
--------------------------------------------------

Use the existing MusicLinkd accessibility system.

Ensure:

• Touch targets
• Contrast
• Screen reader labels
• Clear hierarchy
• Accessible forms
• Meaningful icons
• No colour-only communication

--------------------------------------------------
48. COMPONENT SYSTEM
--------------------------------------------------

Create/reuse a coherent Marketplace component family.

Required components include:

Marketplace Header

Search Field

Category Tile

Marketplace Listing Card

Product Card

Service Card

Rental Card

Studio Card

Seller Card

Verification Badge

Price Block

Condition Badge

Location Row

Rating

Save Button

Filter Sheet

Sort Sheet

Offer Card

Booking Card

Availability Picker

Review Card

Gallery

Seller Profile Summary

Listing Status

Empty State

Marketplace Skeleton

Create Listing Form

Listing Preview

Do not create duplicate versions of components that already exist in the MusicLinkd design system.

--------------------------------------------------
49. TYPOGRAPHY
--------------------------------------------------

Follow the existing MusicLinkd Gestalt typography tokens exactly.

Do not introduce Inter Tight, Inter, or another font merely because previous concept prompts used it.

The existing MusicLinkd typography system takes precedence.

Typography should create:

Clear hierarchy

Editorial character

Strong product identity

Excellent scanability

--------------------------------------------------
50. COLOUR
--------------------------------------------------

Do not hardcode the colours from older prompts.

Use the current MusicLinkd Gestalt semantic colour tokens.

Marketplace should inherit the existing MusicLinkd brand.

Do not introduce a separate Marketplace colour palette.

--------------------------------------------------
51. ELEVATION / SURFACES
--------------------------------------------------

Use the existing Gestalt surface and elevation tokens.

Avoid excessive shadows.

Use elevation to establish hierarchy:

• floating controls
• bottom sheets
• prominent cards
• contextual actions

The Marketplace should feel tactile without becoming skeuomorphic.

--------------------------------------------------
52. PHOTOGRAPHY
--------------------------------------------------

Photography is important.

Use realistic music-industry imagery:

• guitars
• keyboards
• microphones
• drum kits
• recording equipment
• studios
• rehearsal spaces
• Indian instruments
• musicians
• professional music environments

Avoid generic ecommerce stock photography.

Avoid images that look AI-generated.

Use the existing project's approved media assets where available.

--------------------------------------------------
53. LANGUAGE / REGIONAL DESIGN
--------------------------------------------------

MusicLinkd is India-first.

Design for:

₹ pricing

Indian locations

Indian instruments

Indian languages

Regional music communities

Local sellers

Local studios

Do not make the interface culturally generic.

The UI itself may remain English initially, but the information architecture must support localisation.

--------------------------------------------------
54. KEY USER JOURNEYS
--------------------------------------------------

Design the Marketplace around these journeys.

JOURNEY 01 — BUY

Discover

→ Search

→ Filter

→ Listing

→ Seller

→ Message

→ Make Offer

→ Accept

→ Complete

JOURNEY 02 — SELL

Market

→ Sell

→ Choose type

→ Add media

→ Add information

→ Preview

→ Publish

→ Receive enquiry

→ Negotiate

→ Complete

JOURNEY 03 — RENT

Discover

→ Rental

→ Item

→ Availability

→ Request

→ Message

→ Confirm

JOURNEY 04 — SERVICE

Search service

→ Provider

→ Portfolio

→ Reviews

→ Enquire

→ Quote

→ Book

JOURNEY 05 — STUDIO

Discover

→ Studio

→ Gallery

→ Equipment

→ Availability

→ Booking

JOURNEY 06 — NEARBY

Market

→ Nearby

→ Location

→ Listing

→ Seller

→ Enquire

JOURNEY 07 — SAVED

Discover

→ Save

→ Saved

→ Revisit

→ Enquire / Buy / Book

JOURNEY 08 — OFFER

Listing

→ Make Offer

→ Offer sent

→ Messaging

→ Accepted / Declined

→ Complete

--------------------------------------------------
55. DESIGN QUALITY BAR
--------------------------------------------------

The final experience should feel comparable in UX quality to:

Facebook Marketplace
+
Airbnb
+
Spotify
+
Apple
+
Behance

but visually and structurally remain unmistakably:

MUSICLINKD.

Do not copy these products.

Use them as quality references.

--------------------------------------------------
56. IMPORTANT — EXISTING PRODUCT
--------------------------------------------------

This is not a greenfield product.

Before designing anything:

Inspect the existing MusicLinkd:

• Gestalt Design System
• Styleguide
• Components
• Navigation
• Marketplace screens
• Messaging flow
• Profile flow
• Studio flow
• Booking flow
• Opportunities flow
• Existing terminology
• Existing tokens
• Existing iconography

Reuse established patterns.

If an existing MusicLinkd component already solves a problem, use it.

If the existing design system conflicts with this prompt, the existing MusicLinkd design system wins.

--------------------------------------------------
57. DO NOT DESIGN THESE AS SEPARATE PRODUCTS
--------------------------------------------------

Marketplace must connect visually and conceptually with:

Home

Discover

Connections

Messaging

Profiles

Studios

Bookings

Opportunities

Portfolio

Notifications

The user should feel that they are moving through ONE MusicLinkd ecosystem.

--------------------------------------------------
58. FINAL DELIVERABLE
--------------------------------------------------

Design the complete Marketplace experience as one cohesive Android product.

Include:

Marketplace Home

Search

Categories

Filters

Sort

Listing Results

Listing Detail

Seller Profile

Services

Instruments

Equipment

Rentals

Studios

Nearby

Saved

Offers

Messaging handoff

Create Listing

Listing Preview

My Listings

Reviews

Reporting

Availability

Booking handoff

Marketplace Insights

Empty States

Loading States

Error States

Notifications

All necessary bottom sheets, dialogs and contextual states.

Use the existing MusicLinkd Gestalt Design System throughout.

Do not create wireframes.

Do not create disconnected concept screens.

Do not create generic ecommerce UI.

Create a coherent, high-fidelity, production-ready 2026 Android Marketplace experience.

The final result should demonstrate that MusicLinkd is not merely selling music equipment.

It is building the commerce layer of India's professional music ecosystem.



MUSICLINKD MARKETPLACE — FULL IMPLEMENTATION PROMPT
Build/refine the Marketplace feature of the MusicLinkd Android application.
This is an existing product, not a greenfield marketplace. Before changing anything, inspect the existing repository, design system, Firebase/Firestore models, navigation, feature flags, documentation and current Marketplace implementation.
The existing MusicLinkd design system is the single source of truth. Do not create a separate visual language.


1. PRODUCT PURPOSE
MusicLinkd Marketplace is a marketplace for the music ecosystem.
It should allow users to discover, evaluate and interact with music-related products, equipment, services and opportunities while keeping the experience connected to the broader MusicLinkd network.
Marketplace should feel like a natural part of MusicLinkd:
Discover → Evaluate → Connect → Enquire / Buy / Book
It should NOT feel like a generic e-commerce store.
The primary value is:
Find the right music-related thing or service from the right people in the MusicLinkd ecosystem.


2. NAVIGATION
The existing bottom navigation has five destinations:

1. Home
2. Discover
3. Connections
4. Market
5. Profile

Market is the fourth destination.
Do not introduce another bottom-navigation item.
The Marketplace should have its own navigation stack and preserve the existing app navigation architecture.
Use the existing Navigation Compose implementation.


3. MARKETPLACE LANDING SCREEN
Create/refine:
MarketplaceScreen
The screen should immediately communicate:
Music gear, services and opportunities from the music community.
Header
Use the existing MusicLinkd screen-header pattern.
Include:

* Back/navigation behaviour where appropriate
* MusicLinkd branding
* Market
* Search
* Optional notifications/profile affordance according to the existing system

Do not invent a new header style.


4. MARKETPLACE DISCOVERY
The primary Marketplace experience should be discovery-first.
Include:
Search
Search across:

* Product title
* Service title
* Category
* Instrument
* Brand
* Location
* Seller/provider
* Relevant tags

Example:
Search gear, services, studios...
Search must use the existing search component from the design system.


5. CATEGORY SYSTEM
Use the existing ecosystem taxonomy where already documented.
Potential Marketplace categories include:
Instruments

* Guitar
* Bass
* Keyboard
* Piano
* Harmonium
* Sitar
* Tabla
* Percussion
* Drums
* Flute
* Saxophone
* Other instruments

Audio

* Microphones
* Audio interfaces
* Mixers
* Monitors
* Headphones
* Cables
* Recording equipment

Studio

* Studio equipment
* Recording services
* Mixing
* Mastering
* Production
* Rehearsal
* Session services

Creative Services

* Music production
* Vocal recording
* Session musicians
* Mixing
* Mastering
* Songwriting
* Composition
* Sound design

Other
Use the repository's existing Marketplace taxonomy if one already exists.
Do not create conflicting taxonomies.


6. LISTING CARDS
Create/reuse a canonical:
MarketplaceItemCard
The card should support the existing MarketplaceItem data model.
Display only useful information:

* Image
* Item/service title
* Category
* Seller/provider
* Location where relevant
* Price or pricing state
* Condition where relevant
* Verification state where relevant
* Save/favourite
* Relevant CTA

Examples:
Product
Fender Stratocaster
Used · Mumbai
₹85,000
Service
Professional Mixing & Mastering
by Arjun Mehta
Starting ₹3,500
Do not make every card visually identical if the underlying listing type differs.
Use variants of the same component.


7. MARKETPLACE LISTING TYPES
The architecture should support at minimum:
PRODUCT
Physical music-related item.
Examples:

* Guitar
* Microphone
* Keyboard
* Interface
* Speakers

SERVICE
A person/business offering a music-related service.
Examples:

* Mixing
* Mastering
* Production
* Session musician
* Vocal recording

SPACE / OPPORTUNITY
Where supported by the existing data model.
Examples:

* Rehearsal space
* Recording space
* Equipment rental

Do not invent backend entities if the repository does not support them.
Use the existing MarketplaceItem model and extend it only when genuinely necessary.


8. FILTERS
Provide filtering appropriate to the listing type.
Possible filters:

* Category
* Subcategory
* Price
* Location
* Distance
* Condition
* Availability
* Seller type
* Verified seller
* Service type

Use bottom-sheet filtering rather than creating a completely new filter screen unless the existing architecture requires otherwise.


9. SORTING
Support relevant sorting:

* Recommended
* Newest
* Price: Low → High
* Price: High → Low
* Nearest
* Popular

Do not expose meaningless sorting options for services.


10. LISTING DETAIL
Create/refine:
MarketplaceItemDetailScreen
Structure:
Media
Large image/media gallery.
Identity

* Title
* Category
* Seller/provider
* Verification
* Location

Description
Full listing description.
Pricing
Clearly distinguish:

* Fixed price
* Starting price
* Price on enquiry
* Negotiable

Details
Display structured attributes appropriate to the item.
For example:
Instrument

* Brand
* Model
* Condition
* Year
* Location

Service

* Service type
* Experience
* Turnaround
* Location/remote
* Starting price



11. SELLER / PROVIDER
The Marketplace must remain connected to MusicLinkd identities.
Seller section should include:

* Avatar
* Name
* Verification state
* MusicLinkd profile
* Relevant reputation/signals
* CTA to view profile

Primary action:
Connect / Enquire
Do not create a completely separate seller account system.
The seller should map to an existing MusicLinkd user/business identity.


12. COMMUNICATION
Marketplace interactions should use the existing MusicLinkd connection/messaging architecture where possible.
Examples:
Enquire
→ creates/opens conversation
Contact Seller
→ existing messaging system
Connect
→ existing connection system
Do NOT create a second messaging system specifically for Marketplace.


13. BUY / TRANSACTION STATE
Do not invent payment infrastructure if it does not already exist.
If payment is not implemented:
Use:
Enquire
Contact Seller
Request Booking
or another appropriate existing action.
Do not display fake checkout/payment success flows.
If the repository already contains a payment/booking integration, use that integration instead of creating a duplicate.


14. SELL / CREATE LISTING
Provide:
Sell / List Something
from Marketplace.
Create/refine:
CreateMarketplaceListingScreen
Flow:
Step 1
Choose:

* Product
* Service
* Other supported type

Step 2
Add:

* Images
* Title
* Description

Step 3
Category
Step 4
Pricing
Step 5
Location / availability
Step 6
Review
Step 7
Publish
Use the existing Firebase Storage integration for media.
Use Firestore for listing data.
Do not store media blobs directly inside Firestore.


15. FIREBASE / FIRESTORE
Use the existing Firebase project:
musiclinkd-7ae30
Do not create another Firebase project.
Use the existing Firestore architecture.
The existing documented entity is:
MarketplaceItem
Before implementation:

1. Read the existing MarketplaceItem model.
2. Read Firestore schema documentation.
3. Read Firestore security rules.
4. Read existing repositories.
5. Read existing feature-access rules.

Do not create a competing Marketplace collection if one already exists.
Use the repository's canonical collection name.


16. STORAGE
Marketplace media must use Firebase Storage.
Use the existing storage configuration.
Recommended logical structure if consistent with the existing repository:
marketplace/{userId}/{listingId}/...
Do not expose credentials or hardcode secrets.


17. OFFLINE SUPPORT
The application already uses:
Room + Firestore hybrid architecture.
Marketplace should follow that architecture.
Use:
Firestore
↓
Repository
↓
Room cache
↓
ViewModel
↓
Compose UI
The UI should remain useful when network connectivity is temporarily unavailable.
Do not introduce a separate caching system.


18. VIEWMODEL / STATE
Follow the existing UDF architecture.
Expected structure:
MarketplaceScreen
        ↓
MarketplaceViewModel
        ↓
MarketplaceRepository
        ↓
Room / Firestore

Use StateFlow and the existing state conventions.
Avoid screen-local business logic.


19. STATES
Marketplace must explicitly handle:
Loading
Use the existing skeleton components.
Loaded
Show listings.
Empty
Example:
No listings found.
Provide useful recovery:
Clear filters
or
Browse all
Offline
Use the existing offline pattern.
Error
Use the existing Error Catalogue and error-handling architecture.
Do not create arbitrary error messages.


20. SAVED ITEMS
Users should be able to save/favourite Marketplace listings if this capability is already supported by the existing data architecture.
Use the existing interaction pattern.
Do not introduce a second favourites system.


21. ROLE / ACCESS CONTROL
Respect the existing:

* Feature Access Matrix
* Platform Roles Matrix
* Permissions Matrix
* Firebase Security Rules

Marketplace access must follow those documents.
Do not assume every role can:

* Create listings
* Edit listings
* Delete listings
* Contact sellers
* Manage other users' listings

Admin/moderation permissions belong to the Admin Portal, not the Android Marketplace UI.


22. ADMIN INTEGRATION
Every Marketplace listing should be compatible with the existing Admin Portal architecture.
Admin must eventually be able to:

* View listings
* Review listings
* Moderate listings
* Remove listings
* Manage reports
* Review sellers/providers

Do not build admin UI inside the Android application.
Only ensure the data model and status fields support the existing admin architecture.


23. MODERATION
Marketplace listings must support moderation states according to the existing backend architecture.
If the repository already defines statuses, use them.
Do not invent duplicate statuses.
Typical lifecycle:
DRAFT
 ↓
SUBMITTED
 ↓
PENDING_REVIEW
 ↓
PUBLISHED
 ↓
SOLD / COMPLETED / ARCHIVED

Only introduce states that are compatible with the existing schema.


24. ANALYTICS
Use the existing Analytics Event Taxonomy.
Do not invent a second analytics naming convention.
Marketplace events should cover relevant interactions such as:

* Marketplace opened
* Search performed
* Filter applied
* Listing viewed
* Listing saved
* Seller viewed
* Enquiry started
* Listing created
* Listing submitted
* Listing published
* Listing reported

Before adding events, read:
docs/analytics/Analytics_Event_Taxonomy.md
and extend it consistently if Marketplace events are missing.


25. NOTIFICATIONS
Use the existing FCM infrastructure.
Potential Marketplace notifications:

* Listing approved
* Listing rejected
* Listing enquiry received
* Seller responded
* Listing saved / relevant update where supported
* Moderation action

Use the existing notification matrix and channels.
Do not create a separate notification framework.


26. AI
AI is enabled:
ENABLE_AI=true
Do not add unnecessary AI features simply because Gemini exists.
Where the existing product specification supports AI recommendations, Marketplace may use the existing AI infrastructure for:

* Relevant listing recommendations
* Personalised discovery
* Matching services to user needs

Do NOT invent an AI shopping assistant unless the repository specification supports it.


27. DESIGN SYSTEM
The MusicLinkd HTML styleguide is the visual source of truth.
Translate its rules into Jetpack Compose.
Do not use generic Material 3 styling where it conflicts with MusicLinkd.
Respect:

* Typography
* Colour tokens
* Spacing
* Radius
* Borders
* Elevation
* Buttons
* Chips
* Cards
* Bottom sheets
* Navigation
* Icons
* Motion
* Accessibility

Do not introduce arbitrary:
Color(...)
padding(...)
RoundedCornerShape(...)
fontSize(...)

when a canonical MusicLinkd token already exists.


28. MOTION
Marketplace should use the existing MusicLinkd motion system.
Examples:

* Listing card entrance
* Image gallery transition
* Filter sheet presentation
* Save/favourite feedback
* Publish confirmation
* Loading transitions

Motion should be subtle and purposeful.
Do not create a new animation language.


29. ACCESSIBILITY
Follow the existing Accessibility Implementation Checklist.
Ensure:

* Minimum touch target
* Screen-reader labels
* Sufficient contrast
* Meaningful content descriptions
* Keyboard/accessibility navigation where applicable
* No information communicated by colour alone



30. RESPONSIVE / DEVICE BEHAVIOUR
The Marketplace must work across supported Android screen sizes.
Do not hardcode dimensions based on one screenshot.
Use the existing responsive/token system.


31. COMPONENT REUSE
Before creating a component, search the repository.
Reuse existing:

* Cards
* Buttons
* Chips
* Search fields
* Bottom sheets
* Avatars
* Badges
* Empty states
* Error states
* Loading skeletons
* Navigation components

If a reusable Marketplace-specific component is required, create it once and reuse it.


32. FILE MODULARITY
Respect the existing engineering limits:
Screen       300–500 lines preferred
Component    150–250 lines preferred
ViewModel    300–500 lines preferred
Repository   300–500 lines preferred

Do not create another giant Marketplace screen.
Break the feature into focused components.


33. DOCUMENTATION
Update the relevant existing documentation rather than creating unnecessary new Markdown files.
Update:

* Marketplace feature documentation
* Feature Access Matrix
* Analytics Event Taxonomy
* Notification Matrix
* Database/Data Model documentation
* Navigation documentation
* Module dependency documentation
* Design-system documentation where required

Follow the repository governance rule:
No implementation, bug fix, refactor, feature, UI update or documentation update is complete until update.md, CHANGELOG.md and all relevant documentation have been updated.


34. IMPLEMENTATION ORDER
Do NOT attempt everything simultaneously.
Implement in this order:
Phase 1
Audit existing Marketplace code and documentation.
Phase 2
Marketplace navigation + landing screen.
Phase 3
Search + categories + filters.
Phase 4
Listing cards + listing detail.
Phase 5
Seller/provider identity + enquiry.
Phase 6
Create listing.
Phase 7
Firebase Storage + Firestore integration.
Phase 8
Room offline caching.
Phase 9
Analytics + notifications.
Phase 10
Permissions + moderation compatibility.
Phase 11
Accessibility + motion + responsive audit.
Phase 12
Documentation + build verification.


35. IMPORTANT — BEFORE CODING
First inspect the repository and report:

1. Existing Marketplace screens
2. Existing Marketplace components
3. Existing Marketplace ViewModels
4. Existing Marketplace repositories
5. Existing MarketplaceItem model
6. Existing Firestore collection
7. Existing Storage configuration
8. Existing Marketplace documentation
9. Existing navigation route
10. Existing analytics events
11. Existing notification events
12. Existing role/feature restrictions
13. Existing design-system components that can be reused
14. What is genuinely missing

Then produce a concise implementation plan.
Only after the audit should implementation begin.
Do not fabricate missing backend functionality.
Do not replace working integrations with mocks.
Do not create duplicate models, repositories, navigation, analytics or notification systems.
The objective is a production-connected MusicLinkd Marketplace, not a visual prototype.
