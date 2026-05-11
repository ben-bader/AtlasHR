import org.antlr.v4.runtime.misc.NotNull;

import com.hrms.attendance_service.common.enums.VerificationMethod;

public class CheckInRequestDTO {

    @NotBlank
    private String employeeId;

    @NotNull
    private VerificationMethod method;

    @NotNull
    private VerificationPayloadDTO verificationPayload;
}
