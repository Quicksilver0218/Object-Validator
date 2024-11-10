namespace Quicksilver.ObjectValidator;
public class ValidationFailure
{
    public readonly int? id;
    public readonly string? message;

    internal ValidationFailure(int? id, string? message)
    {
        this.id = id;
        this.message = message;
    }
}