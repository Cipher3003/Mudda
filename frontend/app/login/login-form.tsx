export function LoginForm({
  action,
  children,
}: {
  action: any;
  children: React.ReactNode;
}) {
  return (
    <form
      action={action}
      className="flex flex-col space-y-4 bg-gray-50 px-4 py-8 sm:px-16"
    >
      <InputGroup
        label="Username"
        id="username"
        placeholder="iamuser"
        autoComplete="username"
        required
      />
      <InputGroup label="Password" id="password" type="password" required />
      <InputGroupCheckbox label="Remember me" id="remember-me" />
      {children}
    </form>
  );
}

function InputGroup({ label, id, ...props }: any) {
  return (
    <div>
      <label htmlFor={id} className="block text-xs text-gray-600 uppercase">
        {label}
      </label>
      <input
        id={id}
        name={id}
        className="mt-1 block w-full appearance-none rounded-md border border-gray-300 px-3 py-2
         placeholder-gray-400 shadow-sm focus:border-black focus:outline-none 
         focus:ring-black sm:text-sm"
        {...props}
      />
    </div>
  );
}

function InputGroupCheckbox({ label, id, ...props }: any) {
  return (
    <div className="flex items-center">
      <input
        id={id}
        name={id}
        type="checkbox"
        className="h-4 w-4 rounded border-gray-300 text-black focus:ring-black"
        {...props}
      />
      <label htmlFor={id} className="ml-2 block text-sm text-gray-900">
        {label}
      </label>
    </div>
  );
}
