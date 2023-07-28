package dev.razorni.core.profile.option;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ProfileOptions {

	private boolean publicChatEnabled = true;
	private boolean privateChatEnabled = true;
	private boolean privateChatSoundsEnabled = true;
	private boolean friendRightClickEnabled = true;
	private boolean tipsEnabled = true;
	private boolean friendRequestsEnabled = true;
	private boolean frozen = false;
	private boolean soundsEnabled = true;
}
