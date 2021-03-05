import './FormInput.css';
import './FormButton.css';

export const FormInput = ({ type, title, value, onChange, required }) => {
    return (
        <>
            <label htmlFor={title}>{title}</label>
            <input
                type={type ? type : "text"}
                id={title}
                name={title}
                onChange={onChange}
                value={value}
                required={required ? required : true}
            />
        </>
    );
};

export const FormButton = ({ title }) => {
    return (
        <input type="submit" name={title} />
    );
};
