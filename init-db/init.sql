-- Create the schema if it doesn't exist
CREATE SCHEMA IF NOT EXISTS public;

-- Grant all permissions to your healenium user
ALTER ROLE healenium_user SET search_path TO public;
GRANT ALL ON SCHEMA public TO healenium_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO healenium_user;