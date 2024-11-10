namespace Quicksilver.ObjectValidator;
public class ValidationResult
{
    public readonly bool passed;
    public readonly ISet<string> failedFields;
    public readonly ICollection<ValidationFailure> failures;

    internal ValidationResult(bool passed, ISet<string> failedFields, ICollection<ValidationFailure> failures)
    {
        this.passed = passed;
        this.failedFields = failedFields;
        this.failures = failures;
    }
}