# Cross-Platform Mapping Matrix

Unified component implementation matrix mapping UI elements across Web, Android Jetpack Compose, and Admin surfaces:

| Component | Web (HTML5 / Vanilla CSS) | Android (Jetpack Compose M3) | Admin Portal (React / Tailwind) |
| :--- | :--- | :--- | :--- |
| **Primary Button** | `<button class="btn btn-p">` | `Button(colors = ButtonDefaults.buttonColors(containerColor = Pink))` | `<Button variant="primary">` |
| **Secondary Button** | `<button class="btn btn-s">` | `OutlinedButton(border = BorderStroke(1.dp, Line))` | `<Button variant="outline">` |
| **Artist Card** | `<div class="card-s artist-card">` | `Card(shape = RoundedCornerShape(24.dp))` | `<Card className="rounded-3xl border">` |
| **Waveform Player** | `<div class="wf-player">` | `WaveformPlayer(audioUri = uri, playerManager = audioManager)` | `<WaveformPlayer />` |
| **Filter Chip** | `<span class="chip">` | `FilterChip(selected = isSelected, onClick = {})` | `<Badge variant="chip">` |
| **Status Badge** | `<span class="badge badge-av">` | `SuggestionChip(label = { Text("Verified") })` | `<Badge variant="success">` |
| **Search Input** | `<input class="form-control">` | `OutlinedTextField(value = text, onValueChange = {})` | `<Input type="search">` |
| **Bottom Navigation** | `<nav class="bot-nav">` | `NavigationBar { NavigationBarItem(...) }` | `<SidebarNav />` |
